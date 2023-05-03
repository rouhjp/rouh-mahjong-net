package jp.rouh.mahjong.miner;

import jp.rouh.mahjong.score.Meld;
import jp.rouh.mahjong.tile.Side;
import jp.rouh.mahjong.tile.Tile;
import jp.rouh.mahjong.tile.Tiles;

import java.util.ArrayList;
import java.util.List;

/**
 * オンライン麻雀天鳳のデータを扱うためのユーティリティクラス.
 * @author Rouh
 * @version 1.0
 */
public final class TenhoUtils {
    private TenhoUtils() {
        throw new AssertionError("instantiate utility class");
    }

    private static final List<String> TENHO_HAND_TYPE_NAMES = List.of(
            //// 一飜
            "門前清自摸和","立直","一発","槍槓","嶺上開花",
            "海底摸月","河底撈魚","平和","断幺九","一盃口",
            "自風 東","自風 南","自風 西","自風 北",
            "場風 東","場風 南","場風 西","場風 北",
            "役牌 白","役牌 發","役牌 中",
            //// 二飜
            "両立直","七対子","混全帯幺九","一気通貫","三色同順",
            "三色同刻","三槓子","対々和","三暗刻","小三元","混老頭",
            //// 三飜
            "二盃口","純全帯幺九","混一色",
            //// 六飜
            "清一色",
            //// 満貫
            "人和",
            //// 役満
            "天和","地和","大三元","四暗刻","四暗刻単騎","字一色",
            "緑一色","清老頭","九蓮宝燈","純正九蓮宝燈","国士無双",
            "国士無双１３面","大四喜","小四喜","四槓子",
            //// 懸賞役
            "ドラ","裏ドラ","赤ドラ"
    );

    /**
     * 天鳳の麻雀牌のコードから牌を取得します。
     * @param tileCode 天鳳麻雀牌コード
     * @return 牌
     */
    static Tile toTile(int tileCode) {
        assert tileCode >= 0 && tileCode < 136;
        int tileKind = tileCode / 4;
        var tile = Tiles.valueOf(tileKind);
        if (tileCode % 4 == 0) {
            return Tiles.toPrisedRedIfExists(tile);
        }
        return tile;
    }

    /**
     * 天鳳の面子のコードから面子を取得します。
     * @param m 天鳳面子コード
     * @return 面子
     */
    static Meld toMeld(int m) {
        var from = switch (m & 3) {
            case 0 -> Side.SELF;
            case 1 -> Side.RIGHT;
            case 2 -> Side.ACROSS;
            case 3 -> Side.LEFT;
            default -> throw new AssertionError();
        };
        if ((m & (1 << 2)) != 0) {
            //下から3桁目が1の場合は順子
            int calledTileIndex = ((m & 0xFC00) >> 10) % 3;
            int tileTypeCode = (((m & 0xFC00) >> 10) / 3) / 7;
            int tileSuit = (((m & 0xFC00) >> 10) / 3) % 7;
            int tileKindCode = tileTypeCode * 9 + tileSuit;
            var tiles = new ArrayList<Tile>(3);
            tiles.add(toTile(tileKindCode * 4 + ((m & 0x0018) >> 3)));
            tiles.add(toTile((tileKindCode + 1) * 4 + ((m & 0x0060) >> 5)));
            tiles.add(toTile((tileKindCode + 2) * 4 + ((m & 0x0180) >> 7)));
            var calledTile = tiles.remove(calledTileIndex);
            return Meld.ofCallStraight(tiles, calledTile);
        } else if ((m & (1 << 3)) != 0) {
            //下から4桁目が1の場合は刻子
            int calledTileIndex = ((m & 0xFE00) >> 9) % 3;
            int tileKindCode = ((m & 0xFE00) >> 9) / 3;
            int tileCode = tileKindCode * 4;
            int unusedTileIndex = (m & (0x0060)) >> 5;
            var tiles = new ArrayList<Tile>(3);
            for (int i = 0; i < 4; i++) if (i != unusedTileIndex) tiles.add(toTile(tileCode + i));
            var calledTile = tiles.remove(calledTileIndex);
            return Meld.ofCallTriple(tiles, calledTile, from);
        } else if ((m & (1 << 4)) != 0) {
            //下から5桁目が1の場合は加槓
            int calledTileIndex = ((m & 0xFE00) >> 9) % 3;
            int tileKindCode = ((m & 0xFE00) >> 9) / 3;
            int tileCode = tileKindCode * 4;
            int addedTileIndex = (m & (0x0060)) >> 5;
            var tiles = new ArrayList<Tile>(4);
            for (int i = 0; i < 4; i++) tiles.add(toTile(tileCode + i));
            var addedTile = tiles.remove(addedTileIndex);
            var calledTile = tiles.remove(calledTileIndex);
            return Meld.ofAddQuad(Meld.ofCallTriple(tiles, calledTile, from), addedTile);
        } else if ((m & (1 << 5)) != 0) {
            //下から6桁目が1の場合は北ドラ
            throw new AssertionError();
        } else {
            //暗槓もしくは大明槓
            int tileKindCode = ((m & 0xFF00) >> 8) / 4;
            int tileCode = tileKindCode * 4;
            int callTileIndex = ((m & 0xFF00) >> 8) % 4;
            if (from == Side.SELF) {
                var tiles = new ArrayList<Tile>(4);
                tiles.add(toTile(tileCode));
                tiles.add(toTile(tileCode + 1));
                tiles.add(toTile(tileCode + 2));
                tiles.add(toTile(tileCode + 3));
                return Meld.ofSelfQuad(tiles);
            }
            var tiles = new ArrayList<Tile>(4);
            for (int i = 0; i < 4; i++) tiles.add(toTile(tileCode + i));
            var calledTile = tiles.remove(callTileIndex);
            return Meld.ofCallQuad(tiles, calledTile, from);
        }
    }

    public static String toTenhoHandTypeName(int code){
        return TENHO_HAND_TYPE_NAMES.get(code);
    }
}
