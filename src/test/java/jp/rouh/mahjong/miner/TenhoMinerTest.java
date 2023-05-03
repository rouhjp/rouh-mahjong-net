package jp.rouh.mahjong.miner;

import jp.rouh.mahjong.score.StandardHandScoreCalculator;
import jp.rouh.mahjong.score.WinningOption;
import jp.rouh.mahjong.score.WinningSituation;
import jp.rouh.mahjong.tile.Wind;
import jp.rouh.util.DocumentReader;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TenhoMinerTest{

    @Test
    public void testAll() {
        int count = 0;
        var calculator = new StandardHandScoreCalculator();
        var documentReader = new DocumentReader(".cache");
        
        var tableGroupFileListUrl = "http://tenhou.net/sc/raw/list.cgi";
        var tableGroupFileListDoc = documentReader.getDocument(tableGroupFileListUrl);

        var tableGroupFilePattern = Pattern.compile("scc[0-9]+\\.html\\.gz");
        var tableGroupFileMatcher = tableGroupFilePattern.matcher(tableGroupFileListDoc);
        var tableGroupNames = new ArrayList<String>();
        while (tableGroupFileMatcher.find()) {
            tableGroupNames.add(tableGroupFileMatcher.group());
        }

        var tableIds = new ArrayList<String>();
        for (var tableGroupName : tableGroupNames) {
            var tableGroupFileDownloadUrl = "http://tenhou.net/sc/raw/dat/" + tableGroupName;
            var tableGroupDoc = documentReader.getGZippedDocument(tableGroupFileDownloadUrl);

            for (var line : tableGroupDoc.split("\n")) {
                var tableId = extract(line, "log=", "\"");
                tableIds.add(tableId);
            }
        }

        analyze:
        for (var tableId : tableIds) {
            var tableLogUrl = "http://tenhou.net/0/log/?" + tableId;
            var tableViewUrl = "http://tenhou.net/0/?log=" + tableId;
            var tableLogDoc = documentReader.getDocument(tableLogUrl);

            var checked = false;
            var initLine = "";
            for (var line : tableLogDoc.split("\n")) {
                if (!checked && line.contains("<un ")) {
                    boolean threePlayerMahjong = extractAttribute(line, "n3").isEmpty();
                    if (threePlayerMahjong) {
                        continue analyze;
                    }
                    checked = true;
                }
                if (line.contains("<init")) {
                    initLine = line;
                }
                if (line.contains("<agari")) {
                    //var oya = extractAttribute(initLine, "oya");
                    var seed = extractAttribute(initLine, "seed");
                    //var ba = split(extractAttribute(line, "ba"), ",");
                    var hai = split(extractAttribute(line, "hai"), ",");
                    var m = split(extractAttribute(line, "m"), ",");
                    var machi = extractAttribute(line, "machi");
                    var ten = split(extractAttribute(line, "ten"), ",");
                    var yaku = split(extractAttribute(line, "yaku"), ",");
                    var yakuman = split(extractAttribute(line, "yakuman"), ",");
                    var dorahai = split(extractAttribute(line, "dorahai"), ",");
                    var dorahaiUra = split(extractAttribute(line, "dorahaiura"), ",");
                    var who = extractAttribute(line, "who");
                    var fromwho = extractAttribute(line, "fromwho");
                    //var sc = split(extractAttribute(line, "sc"), ",");

                    System.out.println();
                    System.out.println(tableLogUrl);
                    System.out.println(tableViewUrl);
                    System.out.println(line);

                    //int streakCount = Integer.parseInt(ba[0]);
                    //int readyCount = Integer.parseInt(ba[1]);
                    var handTiles = hai.stream().mapToInt(Integer::parseInt).mapToObj(TenhoUtils::toTile)
                            .collect(Collectors.toCollection(ArrayList::new));
                    var openMelds = m.stream().mapToInt(Integer::parseInt).mapToObj(TenhoUtils::toMeld).toList();
                    var winningTile = TenhoUtils.toTile(Integer.parseInt(machi));
                    handTiles.remove(winningTile);

                    var handTypes = IntStream.range(0, yaku.size()).filter(i -> i % 2 == 0).mapToObj(yaku::get)
                            .mapToInt(Integer::parseInt).mapToObj(TenhoUtils::toTenhoHandTypeName).toList();
                    var limitHandTypes = yakuman.stream()
                            .mapToInt(Integer::parseInt).mapToObj(TenhoUtils::toTenhoHandTypeName).toList();

                    var upperIndicators = dorahai.stream().mapToInt(Integer::parseInt).mapToObj(TenhoUtils::toTile).toList();
                    var lowerIndicators = dorahaiUra.stream().mapToInt(Integer::parseInt).mapToObj(TenhoUtils::toTile).toList();

                    var roundWind = Wind.values()[Integer.parseInt(seed.split(",")[0]) / 4];
                    var roundCount = Integer.parseInt(seed.split(",")[0])%4;
                    var seatWind = Wind.values()[(Integer.parseInt(who) + 4 - roundCount)%4];
                    var fromWind = Wind.values()[(Integer.parseInt(fromwho) + 4 - roundCount)%4];

                    int score = Integer.parseInt(ten.get(1));
                    var supplierSide = fromWind.from(seatWind);
                    boolean isReady = handTypes.contains("立直") || handTypes.contains("両立直");
                    boolean isFirstAroundReady = handTypes.contains("両立直");
                    boolean isFirstAroundWin = limitHandTypes.contains("天和") || limitHandTypes.contains("地和");
                    boolean isReadyAroundWin = handTypes.contains("一発");
                    boolean isLastTileRon = handTypes.contains("河底撈魚");
                    boolean isLastTileTsumo = handTypes.contains("海底摸月");
                    boolean isQuadTileRon = handTypes.contains("槍槓");
                    boolean isQuadTileTsumo = handTypes.contains("嶺上開花");

                    var options = new ArrayList<WinningOption>();
                    if (isReady) options.add(WinningOption.READY);
                    if (isFirstAroundReady) options.add(WinningOption.FIRST_AROUND_READY);
                    if (isFirstAroundWin) options.add(WinningOption.FIRST_AROUND_WIN);
                    if (isReadyAroundWin) options.add(WinningOption.READY_AROUND_WIN);
                    if (isLastTileRon || isLastTileTsumo) options.add(WinningOption.LAST_TILE_WIN);
                    if (isQuadTileRon) options.add(WinningOption.QUAD_TILE_WIN);
                    if (isQuadTileTsumo) options.add(WinningOption.QUAD_TURN_WIN);
                    var situation = new WinningSituation(roundWind, seatWind, supplierSide, upperIndicators, lowerIndicators, options);
                    var handScore = calculator.calculate(handTiles, openMelds, winningTile, situation);
                    var calculatedScore = handScore.getPaymentScore();

                    System.out.println("handTypes="+handTypes);
                    System.out.println("upper="+upperIndicators+" lower="+lowerIndicators);
                    System.out.println("roundWind="+roundWind+" seatWind="+seatWind);
                    System.out.println(handTiles + " " + winningTile + " " + openMelds);
                    System.out.println("tenho recorded score: "+score);
                    System.out.println("rmj calculated score: "+calculatedScore);
                    System.out.println("count: "+count++);

                    if (calculatedScore!=score){
                        if (limitHandTypes.contains("四暗刻単騎") || limitHandTypes.contains("純正九蓮宝燈") || limitHandTypes.contains("国士無双１３面")
                        || limitHandTypes.contains("大四喜") || limitHandTypes.contains("四槓子")){
                            if (calculatedScore!=score*2){
                                throw new AssertionError();
                            }
                        }else{
                            throw new AssertionError();
                        }
                    }
                }
            }
        }
    }

    public static String extract(String string, String keyword, String until){
        int fromIndex = string.indexOf(keyword);
        if (fromIndex==-1) return "";
        int fromIndexFixed = fromIndex + keyword.length();
        int toIndex = string.indexOf(until, fromIndexFixed);
        if (toIndex==-1) return string.substring(fromIndexFixed);
        return string.substring(fromIndexFixed, toIndex);
    }

    public static String extractAttribute(String string, String name){
        return extract(string, name+"=\"", "\"");
    }

    public static List<String> split(String string, String delimiter){
        if (string.isEmpty()) return List.of();
        return List.of(string.split(delimiter));
    }
}
