package jp.rouh.mahjong.net;

import jp.rouh.mahjong.game.event.CallAction;
import jp.rouh.mahjong.game.event.CallActionType;
import jp.rouh.mahjong.game.event.TurnAction;
import jp.rouh.mahjong.game.event.TurnActionType;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.util.net.msg.MessageConversionRule;
import jp.rouh.util.net.msg.MessageConverter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RoomMessageConverters{
    private static final MessageConverter INSTANCE;
    static{
        var converter = new MessageConverter();
        converter.addRuleLast(String.class, ofString());
        converter.addRuleLast(Integer.class, ofInteger());
        converter.addRuleLast(Double.class, ofDouble());
        converter.addRuleLast(Boolean.class, ofBoolean());
        converter.addRuleLast(Enum.class, ofEnum());
        converter.addRuleLast(List.class, ofList(converter));
        converter.addRuleLast(Map.class, ofMap(converter));
        converter.addRuleLast(Object.class, ofPojo(converter));

        converter.addRuleFirst(TurnAction.class, ofTurnAction());
        converter.addRuleFirst(CallAction.class, ofCallAction());

        converter.addRuleFirst(Class.class, ofClass());
        converter.addRuleFirst(Method.class, ofMethod());

        INSTANCE = converter;
    }

    private RoomMessageConverters(){
        throw new AssertionError("instantiate utility class");
    }

    public static MessageConverter getConverter(){
        return INSTANCE;
    }

    @SuppressWarnings("rawtypes")
    private static MessageConversionRule<Class> ofClass(){
        return new MessageConversionRule.Builder<Class>()
                .setEncoder(clazz->"class("+clazz.getName()+")")
                .setSelector(str->str.startsWith("class("))
                .setDecoder(str->{
                    try{
                        return classOf(unwrap(str));
                    }catch(ClassNotFoundException e){
                        throw new RuntimeException(e);
                    }
                })
                .build();
    }

    private static MessageConversionRule<Method> ofMethod(){
        return new MessageConversionRule.Builder<Method>()
                .setEncoder(method->{
                    var declaringClass = method.getDeclaringClass().getName();
                    var methodName = method.getName();
                    var parameterTypes = method.getParameterTypes().length==0?"null":
                            "types("+Stream.of(method.getParameterTypes()).map(Class::getName).collect(Collectors.joining(","))+")";
                    return "method("+declaringClass+","+methodName+","+parameterTypes+")";
                })
                .setSelector(str->str.startsWith("method("))
                .setDecoder(str->{
                    try{
                        var params = split(unwrap(str));
                        var declaringClass = classOf(params.get(0));
                        var methodName = params.get(1);
                        var parameterTypes = new ArrayList<Class<?>>();
                        if(!params.get(2).equals("null")){
                            for(var typeString: split(unwrap(params.get(2)))){
                                parameterTypes.add(classOf(typeString));
                            }
                        }
                        return declaringClass.getMethod(methodName, parameterTypes.toArray(new Class[0]));
                    }catch(ClassNotFoundException | NoSuchMethodException e){
                        throw new RuntimeException(e);
                    }
                })
                .build();
    }

    private static MessageConversionRule<CallAction> ofCallAction(){
        return new MessageConversionRule.Builder<CallAction>()
                .setEncoder(callAction->{
                    var actionType = callAction.type();
                    var argument = callAction.hasArguments()?callAction.arguments():null;
                    var firstTile = argument==null?"null":argument.get(0).name();
                    var secondTile = argument==null?"null":argument.get(1).name();
                    return "callAction("+actionType.name()+","+
                            firstTile+","+secondTile+")";
                })
                .setSelector(str->str.startsWith("callAction("))
                .setDecoder(str->{
                    var params = split(unwrap(str));
                    var actionType = CallActionType.valueOf(params.get(0));
                    var firstTile = params.get(1).equals("null")?null:Tile.valueOf(params.get(1));
                    var secondTile = params.get(2).equals("null")?null:Tile.valueOf(params.get(2));
                    return switch(actionType){
                        case PASS -> CallAction.ofPass();
                        case CHI -> CallAction.ofChi(Objects.requireNonNull(firstTile), secondTile);
                        case PON -> CallAction.ofPon(Objects.requireNonNull(firstTile), secondTile);
                        case KAN -> CallAction.ofKan();
                        case RON -> CallAction.ofRon();
                    };
                })
                .build();
    }

    private static MessageConversionRule<TurnAction> ofTurnAction(){
        return new MessageConversionRule.Builder<TurnAction>()
                .setEncoder(turnAction->{
                    var actionType = turnAction.type();
                    var argument = turnAction.hasArgument()?turnAction.argument():null;
                    return "turnAction("+actionType.name()+","+argument+")";
                })
                .setSelector(str->str.startsWith("turnAction("))
                .setDecoder(str->{
                    var params = split(unwrap(str));
                    var actionType = TurnActionType.valueOf(params.get(0));
                    var argument = params.get(1).equals("null")?null:Tile.valueOf(params.get(1));
                    return switch(actionType){
                        case TSUMO -> TurnAction.ofTsumo();
                        case NINE_TILES -> TurnAction.ofNineTiles();
                        case TURN_KAN -> TurnAction.ofKan(argument);
                        case READY_DISCARD -> TurnAction.ofReadyAndDiscard(argument);
                        case DISCARD_DRAWN -> TurnAction.ofDiscardDrawn(argument);
                        case DISCARD_ANY -> TurnAction.ofDiscard(argument);
                    };
                })
                .build();
    }

    private static MessageConversionRule<String> ofString(){
        return new MessageConversionRule.Builder<String>()
                .setEncoder(str->"str("+quote(str)+")")
                .setSelector(str->str.startsWith("str("))
                .setDecoder(str->unquote(unwrap(str)))
                .build();
    }

    private static MessageConversionRule<Integer> ofInteger(){
        return new MessageConversionRule.Builder<Integer>()
                .setEncoder(integer->"int("+integer+")")
                .setSelector(str->str.startsWith("int("))
                .setDecoder(str->Integer.parseInt(unwrap(str)))
                .build();
    }

    private static MessageConversionRule<Double> ofDouble(){
        return new MessageConversionRule.Builder<Double>()
                .setEncoder(number->"double("+number+")")
                .setSelector(str->str.startsWith("double("))
                .setDecoder(str->Double.parseDouble(unwrap(str)))
                .build();
    }

    private static MessageConversionRule<Boolean> ofBoolean(){
        return new MessageConversionRule.Builder<Boolean>()
                .setEncoder(bool->"bool("+bool+")")
                .setSelector(str->str.startsWith("bool("))
                .setDecoder(str->Boolean.parseBoolean(unwrap(str)))
                .build();
    }

    @SuppressWarnings("rawtypes")
    private static MessageConversionRule<Enum> ofEnum(){
        return new MessageConversionRule.Builder<Enum>()
                .setEncoder(constant->"enum("+constant.getClass().getName()+","+constant+")")
                .setSelector(str->str.startsWith("enum("))
                .setDecoder(str->{
                    try{
                        var params = split(unwrap(str));
                        var clazz = classOf(params.get(0));
                        var valueOf = clazz.getMethod("valueOf", String.class);
                        var name = params.get(1);
                        return (Enum)valueOf.invoke(null, name);
                    }catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
                        throw new RuntimeException(e);
                    }
                })
                .build();
    }

    @SuppressWarnings("rawtypes")
    private static MessageConversionRule<List> ofList(MessageConverter converter){
        return new MessageConversionRule.Builder<List>()
                .setEncoder(list->{
                    @SuppressWarnings("unchecked")
                    var objectList = (List<Object>)list;
                    return "list(" + objectList.stream().map(converter::encode)
                            .collect(Collectors.joining(","))+")";
                })
                .setSelector(str->str.startsWith("list("))
                .setDecoder(str->{
                    if(str.equals("list()")){
                        return Collections.emptyList();
                    }
                    var elements = split(unwrap(str));
                    return elements.stream().map(converter::decode)
                            .collect(Collectors.toList());
                })
                .build();
    }

    @SuppressWarnings("rawtypes")
    private static MessageConversionRule<Map> ofMap(MessageConverter converter){
        return new MessageConversionRule.Builder<Map>()
                .setEncoder(map->{
                    @SuppressWarnings("unchecked")
                    var objectMap = ((Map<Object, Object>)map);
                    return "map("+objectMap.entrySet().stream()
                            .map(entry->"("+converter.encode(entry.getKey())+","+converter.encode(entry.getValue())+")")
                            .collect(Collectors.joining(","))+")";
                })
                .setSelector(str->str.startsWith("map("))
                .setDecoder(str->{
                    var map = new HashMap<>();
                    var entryStrings = split(unwrap(str));
                    for(var entryString:entryStrings){
                        var params = split(unwrap(entryString));
                        var key = converter.decode(params.get(0));
                        var value = converter.decode(params.get(1));
                        map.put(key, value);
                    }
                    return map;
                })
                .build();
    }

    private static MessageConversionRule<Object> ofPojo(MessageConverter converter){
        return new MessageConversionRule.Builder<>()
                .setEncoder(pojo->{
                    try{
                        var fieldStrings = new ArrayList<String>();
                        for(var method: pojo.getClass().getDeclaredMethods()){
                            if(method.getDeclaringClass()==pojo.getClass()){
                                var getterPrefix = method.getReturnType()==boolean.class?"is":"get";
                                if(method.getReturnType()!=void.class && method.getName().startsWith(getterPrefix)){
                                    var fieldName = method.getName().substring(getterPrefix.length());
                                    var fieldClass = method.getReturnType().getName();
                                    var fieldValue = method.invoke(pojo);
                                    fieldStrings.add("(" + fieldName + "," + fieldClass + "," + converter.encode(fieldValue) + ")");
                                }
                            }
                        }
                        return "pojo("+ pojo.getClass().getName() + ",fields(" + String.join(",", fieldStrings) +"))";
                    }catch(IllegalAccessException | InvocationTargetException e){
                        throw new RuntimeException("exception caught while encoding object: "+pojo.getClass().getName(), e);
                    }
                })
                .setSelector(str->str.startsWith("pojo("))
                .setDecoder(str->{
                    try{
                        var params = split(unwrap(str));
                        var clazz = classOf(params.get(0));
                        var fieldStrings = split(unwrap(params.get(1)));
                        var constructor = clazz.getDeclaredConstructor();
                        var instance = constructor.newInstance();
                        for(var fieldString:fieldStrings){
                            var fieldParams = split(unwrap(fieldString));
                            var fieldName = fieldParams.get(0);
                            var fieldClass = classOf(fieldParams.get(1));
                            var fieldValue = converter.decode(fieldParams.get(2));
                            var setter = clazz.getMethod("set" + fieldName, fieldClass);
                            setter.invoke(instance, fieldValue);
                        }
                        return instance;
                    }catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                            InvocationTargetException | InstantiationException e){
                        throw new RuntimeException(e);
                    }
                })
                .build();
    }
    
    private static Class<?> classOf(String name) throws ClassNotFoundException{
        return switch(name){
            case "int" -> int.class;
            case "double" -> double.class;
            case "boolean" -> boolean.class;
            default -> Class.forName(name);
        };
    }

    private static List<String> split(String str){
        var substrings = new ArrayList<String>();
        int start = 0;
        int nest = 0;
        boolean escaped = false;
        boolean inQuote = false;
        for(int i = 0; i<str.length(); i++){
            switch(str.charAt(i)){
                case '"' -> {
                    if(!escaped){
                        inQuote = !inQuote;
                    }
                }
                case '\\' -> {
                    if(!escaped){
                        escaped = true;
                        continue;
                    }
                }
                case '(' -> {
                    if(!inQuote){
                        nest++;
                    }
                }
                case ')' -> {
                    if(!inQuote){
                        nest--;
                    }
                }
                case ',' -> {
                    if(!inQuote && nest==0){
                        substrings.add(str.substring(start, i));
                        start = i + 1;
                    }
                }
            }
            escaped = false;
        }
        substrings.add(str.substring(start));
        return substrings;
    }

    private static String unwrap(String str){
        int start = str.indexOf("(");
        int end = str.lastIndexOf(")");
        return str.substring(start + 1, end);
    }

    private static String quote(String str){
        return "\"" + str.replace("\"", "\\\"") + "\"";
    }

    private static String unquote(String str){
        return str.substring(1, str.length() - 1).replace("\\\"", "\"");
    }
}
