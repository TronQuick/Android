package top.gardel.httputils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 由Gardel在2018/2/21使用Android Studio创建.
 */

public class JSONCookieStore implements CookieStore {
    private String savepath;
    private ConcurrentHashMap<URI, List<HttpCookie>> map;
    JSONCookieStore(String path) {
        savepath = path;
        try {
            File jsonfile = new File(path);
            if (!jsonfile.exists()) {
                jsonfile.createNewFile();
                throw new FileNotFoundException(path + " not exists, created it.");
            }
            BufferedReader jsonReader = new BufferedReader(new FileReader(jsonfile));
            StringBuilder jsonContent = new StringBuilder();
            String tmp;
            while ((tmp = jsonReader.readLine()) != null)
                jsonContent.append(tmp).append("\n");
            JSONObject allCookie = new JSONObject(jsonContent.toString());
            map = new ConcurrentHashMap<>();
            Iterator<String> it = allCookie.keys();
            while (it.hasNext()) {
                String url = it.next();
                List<HttpCookie> list = new ArrayList<>();
                JSONArray cookies = allCookie.getJSONArray(url);
                for (int i = 0; i < cookies.length(); i++) {
                    JSONObject obj = cookies.getJSONObject(i);
                    if (obj.getLong("createtime") + obj.getLong("max-age") < System.currentTimeMillis() / 1000) continue;
                    HttpCookie cookie = new HttpCookie(obj.getString("name"), obj.getString("value"));
                    cookie.setDomain(obj.getString("domain"));
                    cookie.setPath(obj.getString("path"));
                    cookie.setMaxAge(obj.getLong("max-age"));
                    list.add(cookie);
                }
                map.put(URI.create(url), list);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (map == null) map = new ConcurrentHashMap<>();
    }

    @Override
    public void add(URI uri, HttpCookie httpCookie) {
        boolean hasUri = false;
        for (ConcurrentHashMap.Entry<URI, List<HttpCookie>> entry : map.entrySet()) {
            URI key = entry.getKey();
            if (key.getHost().equals(uri.getHost()) && key.getScheme().equals(uri.getScheme())) {
				List<HttpCookie> list = entry.getValue();
				ListIterator<HttpCookie> iterator = list.listIterator();
				while (iterator.hasNext()) {
					HttpCookie cookie = iterator.next();
					if (cookie.getName().equals(httpCookie.getName())) {
						iterator.remove();
					}
				}
				list.add(httpCookie);
                hasUri = true;
                break;
            }
        }
        if (!hasUri) {
            List<HttpCookie> list = new ArrayList<>();
            list.add(httpCookie);
            map.put(uri, list);
        }
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        for (ConcurrentHashMap.Entry<URI, List<HttpCookie>> entry : map.entrySet()) {
            URI key = entry.getKey();
            if (key.getHost().equals(uri.getHost()) && key.getScheme().equals(uri.getScheme()))
                return entry.getValue();
        }
        return new ArrayList<>();
    }

    @Override
    public List<HttpCookie> getCookies() {
        ArrayList<HttpCookie> list = new ArrayList<>();
        Collection<List<HttpCookie>> collection = map.values();
        for (List<HttpCookie> aCollection : collection)
            list.addAll(aCollection);
        return list;
    }

    @Override
    public List<URI> getURIs() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public boolean remove(URI uri, HttpCookie httpCookie) {
        return map.containsKey(uri) && map.get(uri).remove(httpCookie);
    }

    @Override
    public boolean removeAll() {
        map.clear();
		return new File(savepath).delete();
    }

    public void write() {
        try {
            File jsonfile = new File(savepath);
            if (!jsonfile.exists()) jsonfile.createNewFile();
            BufferedWriter jsonWriter = new BufferedWriter(new FileWriter(jsonfile));
            JSONObject allCookie = new JSONObject();
            for (ConcurrentHashMap.Entry<URI, List<HttpCookie>> entry : map.entrySet()) {
                JSONArray cookies = new JSONArray();
                for (HttpCookie cookie : entry.getValue()) {
                    if (cookie.hasExpired()) continue;
					JSONObject obj = new JSONObject();
                    obj.put("name", cookie.getName())
						.put("value", cookie.getValue())
						.put("domain", cookie.getDomain())
						.put("path", cookie.getPath())
						.put("createtime", System.currentTimeMillis() / 1000)
						.put("max-age", cookie.getMaxAge());
                    cookies.put(obj);
                }
                allCookie.put(entry.getKey().getScheme() + "://" + entry.getKey().getHost() + "/", cookies);
            }
            jsonWriter.write(allCookie.toString());
            jsonWriter.flush();
        } catch (FileNotFoundException e) {
            // ignored
        } catch (IOException e) {
            // ignored
        } catch (JSONException e) {
            // ignored
        }
    }
}
