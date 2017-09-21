package com.codelanx.aether.common.rest;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.AsyncBot;
import com.codelanx.aether.common.json.item.ItemLoader;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.item.SerializableMaterial;
import com.codelanx.aether.common.json.recipe.RecipeLoader;
import com.codelanx.commons.data.FileSerializable;
import com.codelanx.commons.data.types.Json;
import com.codelanx.commons.logging.Logging;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class RestLoader {

    private static final String URL = "http://runemate.rogue.ninja/";
    private static final Logger LOG = Logger.getLogger("Testing"); //used for some local testing atm, due for removal
    private final AsyncBot bot;
    private final ItemLoader items;
    private final RecipeLoader loader;

    public static final void main(String... args) {
        Logging.setNab(() -> RestLoader.LOG);
    }

    public RestLoader(AsyncBot bot) {
        this.bot = bot;
        this.items = new ItemLoader(bot);
        this.loader = new RecipeLoader(bot);
    }

    //triggers a local load from files
    //this also relieves any static access for backreferencing other data sources (e.g. recipes -> items)
    public void loadLocal() {
        Stream.of(this.items, this.loader).forEach(Loader::loadLocal);
    }

    public Material getItem(int id) {
        return this.getItem(id, null);
    }

    public Material getItem(String name) {
        return this.getItem(-1, name);
    }

    public Material getItem(int id, String name) {
        Material back = this.itemGet(id, name);
        Logging.info("RestLoader#getItem(" + id + "," + name + "): " + back);
        return back;
    }

    //allows us to print about return value
    private Material itemGet(int id, String name) {
        if (id <= 0 && name == null) {
            throw new IllegalArgumentException("Must supply a valid name or id above 0 [" + id + "," + name + "]");
        }
        Material ported = null;
        //is it in the older cache system? (json flatfile)
        if (id > 0) {
            ported = this.getKnownItems().getItem(id);
        }
        if (ported == null && name != null) {
            ported = this.getKnownItems().getItem(name);
        }
        if (ported != null) {
            return ported;
        }
        //can we pull from ItemDefinition?
        if (id > 0) {
            ported = new SerializableMaterial(ItemDefinition.get(id));
        }
        //last chance - hit the rest api
        StringBuilder req = new StringBuilder();
        Supplier<Character> prefix = () -> req.length() > 0 ? '&' : '?';
        if (id > 0) {
            req.append(prefix.get()).append("id=").append(id);
        }
        if (name != null) {
            req.append(prefix.get()).append("name=").append(name);
        }
        return this.request("item", req.toString(), SerializableMaterial.class);
    }

    private <R extends FileSerializable> R request(String requestType, String query, Class<R> type) {
        if (true) {
            return null;
        }
        try {
            URL url = new URL(URL);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type", "application/json");
            http.setUseCaches(false);
            http.setDoOutput(true);
            //TODO: read to string
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String raw = "";
        Json j = new Json(raw);
        return j.mutable().as(type);
    }

    public RecipeLoader getRecipes() {
        return this.loader;
    }

    public ItemLoader getKnownItems() {
        return this.items;
    }
}
