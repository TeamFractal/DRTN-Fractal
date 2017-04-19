package io.github.teamfractal.entity;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Random;


public class CaptureData {
    private static CaptureData _instance;

    public enum AttributeType {
        @SerializedName("Fire")
        Fire,

        @SerializedName("Grass")
        Grass,

        @SerializedName("Water")
        Water
    }

    private static final Random _rnd;

    static {
        _rnd = new Random();
        Gson gson = new GsonBuilder().create();
        _instance =
                gson.fromJson(
                        Gdx.files.internal("capture/data.json")
                                .reader("UTF-8"), CaptureData.class);
    }

    public static CaptureData getData() {
        return _instance;
    }

    public static AttributeType randomType () {
        AttributeType[] val = AttributeType.values();
        return val[_rnd.nextInt(val.length)];
    }

    @Expose
    @SerializedName("strUseMove")
    public String strUseMove;

    @Expose
    @SerializedName("strBeginMasterBall")
    public String strBeginMasterBall;

    @Expose
    @SerializedName("strCaptureSuccess")
    public String strCaptureSuccess;

    @Expose
    @SerializedName("strCaptureFail")
    public String strCaptureFail;

    public AttributeRate getRateFromType(AttributeType type) {
        for (AttributeRate t : attributeRates) {
            if (t.type == type) {
                return t;
            }
        }
        return null;
    }

    @Expose
    @SerializedName("attribute rate")
    public List<AttributeRate> attributeRates = null;

    public class AttributeRate {
        @Expose
        @SerializedName("type")
        public AttributeType type;

        public Against getAgainstRateFromType(AttributeType type) {
            for (Against t : against) {
                if (t.type == type) {
                    return t;
                }
            }
            return null;
        }

        @Expose
        @SerializedName("against")
        public List<Against> against = null;

        public class Against {
            @Expose
            @SerializedName("type")
            public AttributeType type;

            @Expose
            @SerializedName("multiplier")
            public double multiplier;
        }
    }

    public String getCommentFromMultiplier(double multiplier) {
        for (AttributeRateComment comment: attributeRateComments) {
            if (comment.min <= multiplier && multiplier <= comment.max) {
                return comment.comment;
            }
        }
        return null;
    }

    @Expose
    @SerializedName("attribute rate comment")
    public List<AttributeRateComment> attributeRateComments = null;

    public class AttributeRateComment {
        @Expose
        @SerializedName("min")
        public double min;

        @Expose
        @SerializedName("max")
        public double max;

        @Expose
        @SerializedName("comment")
        public String comment;
    }

    @Expose
    @SerializedName("fight actions")
    public List<FightAction> fightActions = null;

    public class FightAction {
        @Expose
        @SerializedName("type")
        public AttributeType type;

        @Expose
        @SerializedName("name")
        public String name;

        @Expose
        @SerializedName("success")
        public String success;

        @Expose
        @SerializedName("fail")
        public List<Fail> fail = null;

        public class Fail {
            @Expose
            @SerializedName("message")
            public String message;

            @Expose
            @SerializedName("cost")
            public Cost cost;

            public class Cost {
                @Expose
                @SerializedName("roboticon")
                public List<Integer> roboticon = null;

                @Expose
                @SerializedName("food")
                public List<Integer> food = null;

                @Expose
                @SerializedName("energy")
                public List<Integer> energy = null;

                @Expose
                @SerializedName("ore")
                public List<Integer> ore = null;

                @Expose
                @SerializedName("money")
                public List<Integer> money = null;
            }
        }
    }
}