package jp.rouh.mahjong.game.event;

import jp.rouh.mahjong.tile.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * 麻雀卓描画を{@link TableStrategy}の実装とするアダプタクラス。
 * <p>与えられた選択肢を順に選択させるプレゼンテーションの標準的な実装を提供します。
 * @author Rouh
 * @version 1.0
 */
public interface TableStrategyAdapter extends TableStrategy{

    /**
     * 手牌をビューが表示している順番で取得します。
     * @return 手牌(自摸牌を含む)
     */
    List<Tile> getShownHandTiles();

    /**
     * プレイヤーが選択肢のうち一つを選択するまで
     * スレッドをブロックして待機し, プレイヤーの選択を返します。
     * @param choices 選択肢
     * @return プレイヤーの入力
     */
    ActionInput waitForInput(List<ActionInput> choices);

    /**
     * プレイヤーが確認を終えるまでスレッドをブロックして待機します。
     */
    void waitForAcknowledge();

    @Override
    default void acknowledge(){
        waitForAcknowledge();
    }

    /**
     * {@inheritDoc}
     * 求められた選択をプレゼンテーションレベルの選択肢{@link ActionInput}に分解し,
     * {@link #waitForInput}メソッドの実装を用いてプレイヤーに選択を求めます。
     * @param choices 選択可能な行動のリスト
     * @return ターン内行動
     */
    @Override
    default TurnAction selectTurnAction(List<TurnAction> choices){
        if(choices.size()==1) return choices.get(0);
        enum SelectMode{
            DISCARD, READY, QUAD
        }
        var mode = SelectMode.DISCARD;
        var player = new TurnActionSelector(choices);
        var tiles = getShownHandTiles();
        while(true){
            var inputChoices = new ArrayList<ActionInput>();
            switch(mode){
                case DISCARD ->{
                    if(player.canDeclareKan()) inputChoices.add(ActionInput.SELECT_KAN);
                    if(player.canDeclareReady()) inputChoices.add(ActionInput.SELECT_READY);
                    if(player.canDeclareTsumo()) inputChoices.add(ActionInput.SELECT_TSUMO);
                    if(player.canDeclareNineTiles()) inputChoices.add(ActionInput.SELECT_NINE_TILES);
                    for(int i = 0; i<tiles.size(); i++){
                        if(player.canSelectForDiscard(tiles.get(i))){
                            inputChoices.add(ActionInput.ofIndex(i));
                        }
                    }
                }
                case READY -> {
                    inputChoices.add(ActionInput.SELECT_CANCEL);
                    for(int i = 0; i<tiles.size(); i++){
                        if(player.canSelectForReady(tiles.get(i))){
                            inputChoices.add(ActionInput.ofIndex(i));
                        }
                    }
                }
                case QUAD -> {
                    inputChoices.add(ActionInput.SELECT_CANCEL);
                    for(int i = 0; i<tiles.size(); i++){
                        if(player.canSelectForKan(tiles.get(i))){
                            inputChoices.add(ActionInput.ofIndex(i));
                        }
                    }
                }
            }
            var input = waitForInput(inputChoices);
            switch(input){
                case SELECT_KAN ->{
                    mode = SelectMode.QUAD;
                    var kanAction = player.getSingleOutKanAction();
                    if(kanAction.isPresent()){
                        return kanAction.get();
                    }
                }
                case SELECT_READY -> mode = SelectMode.READY;
                case SELECT_CANCEL -> mode = SelectMode.DISCARD;
                case SELECT_TSUMO -> {
                    return player.getTsumoAction();
                }
                case SELECT_NINE_TILES -> {
                    return player.getNineTilesAction();
                }
                default -> {
                    var selected = tiles.get(input.getIndex());
                    switch(mode){
                        case QUAD -> {
                            return player.getKanActionOf(selected);
                        }
                        case READY -> {
                            return player.getReadyActionOf(selected);
                        }
                        case DISCARD -> {
                            return player.getDiscardActionOf(selected);
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 求められた選択をプレゼンテーションレベルの選択肢{@link ActionInput}に分解し,
     * {@link #waitForInput}メソッドの実装を用いてプレイヤーに選択を求めます。
     * @param choices 選択可能な行動のリスト
     * @return ターン外行動
     */
    @Override
    default CallAction selectCallAction(List<CallAction> choices){
        if(choices.size()==1) return choices.get(0);
        var player = new CallActionSelector(choices);
        var tiles = getShownHandTiles();
        while(true){
            var inputChoices = new ArrayList<ActionInput>();
            if(player.canDeclareChi()) inputChoices.add(ActionInput.SELECT_CHI);
            if(player.canDeclarePon()) inputChoices.add(ActionInput.SELECT_PON);
            if(player.canDeclareKan()) inputChoices.add(ActionInput.SELECT_KAN);
            if(player.canDeclareRon()) inputChoices.add(ActionInput.SELECT_RON);
            inputChoices.add(ActionInput.SELECT_PASS);
            var firstInput = waitForInput(inputChoices);
            enum SelectMode{
                CHI, PON
            }
            SelectMode mode;
            switch(firstInput){
                case SELECT_PASS -> {
                    return player.getPassAction();
                }
                case SELECT_RON -> {
                    return player.getRonAction();
                }
                case SELECT_KAN -> {
                    return player.getKanAction();
                }
                case SELECT_CHI -> {
                    var chiInput = player.getSingledOutChiAction();
                    if(chiInput.isPresent()){
                        return chiInput.get();
                    }
                    mode = SelectMode.CHI;
                }
                case SELECT_PON -> {
                    var ponInput = player.getSingledOutPonAction();
                    if(ponInput.isPresent()){
                        return ponInput.get();
                    }
                    mode = SelectMode.PON;
                }
                default -> throw new IllegalStateException("invalid input: " + firstInput + " at first input");
            }
            var firstTileInputChoices = new ArrayList<ActionInput>();
            firstTileInputChoices.add(ActionInput.SELECT_CANCEL);
            switch(mode){
                case CHI -> {
                    for(int i = 0; i<tiles.size(); i++){
                        if(player.canSelectForChi(tiles.get(i))){
                            firstTileInputChoices.add(ActionInput.ofIndex(i));
                        }
                    }
                }
                case PON -> {
                    for(int i = 0; i<tiles.size(); i++){
                        if(player.canSelectForPon(tiles.get(i))){
                            firstTileInputChoices.add(ActionInput.ofIndex(i));
                        }
                    }
                }
            }
            var secondInput = waitForInput(firstTileInputChoices);
            if(secondInput==ActionInput.SELECT_CANCEL){
                continue;
            }
            var firstTile = tiles.get(secondInput.getIndex());
            var secondTileInputChoices = new ArrayList<ActionInput>();
            secondTileInputChoices.add(ActionInput.SELECT_CANCEL);
            switch(mode){
                case CHI -> {
                    var chiAction = player.getSingledOutChiAction(firstTile);
                    if(chiAction.isPresent()){
                        return chiAction.get();
                    }
                    for(int i = 0; i<tiles.size(); i++){
                        if(player.canSelectForChiUnderSelection(tiles.get(i), firstTile)){
                            secondTileInputChoices.add(ActionInput.ofIndex(i));
                        }
                    }
                }
                case PON -> {
                    var ponAction = player.getSingledOutPonAction(firstTile);
                    if(ponAction.isPresent()){
                        return ponAction.get();
                    }
                    for(int i = 0; i<tiles.size(); i++){
                        if(player.canSelectForPonUnderSelection(tiles.get(i), firstTile)){
                            secondTileInputChoices.add(ActionInput.ofIndex(i));
                        }
                    }
                }
            }
            var thirdInput = waitForInput(secondTileInputChoices);
            if(thirdInput==ActionInput.SELECT_CANCEL){
                continue;
            }
            var secondTile = tiles.get(thirdInput.getIndex());
            switch(mode){
                case CHI -> {
                    return player.getSingledOutChiAction(firstTile, secondTile);
                }
                case PON -> {
                    return player.getSingledOutPonAction(firstTile, secondTile);
                }
            }
        }
    }
}
