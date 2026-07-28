package io.github.gcjojo.blablalib.client.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelLoader {
    private static final Gson GSON = new GsonBuilder().create();

    public static GeoFile loadModel(String json) {
        return GSON.fromJson(json, GeoFile.class);
    }

    public record GeoFile(
            @SerializedName("format_version") String formatVersion,
            @SerializedName("minecraft:geometry") List<Geometry> geometries){}

    public record GeoDescription(
            String identifier,
            @SerializedName("texture_width") int textureWidth,
            @SerializedName("texture_height") int textureHeight,
            @SerializedName("visible_bound_width") float visibleBoundWidth,
            @SerializedName("visible_bound_height") float visibleBoundHeight,
            @SerializedName("visible_bound_offset") float[] visibleBoundOffset){}

    public record GeoCube(float[] origin,  float[] pivot, float[] rotation, int[] size, int[] uv){}

    public record GeoBone(String name, String parent, float[] pivot, float[] rotation, List<GeoCube> cubes){}

    public record Geometry(GeoDescription description, List<GeoBone> bones){}
}
