/*
 * Clover - 4chan browser https://github.com/Floens/Clover/
 * Copyright (C) 2014  Floens
 * Copyright (C) 2014  wingy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wingy.chan.chan;

import java.util.Locale;

public class ChanUrls {
    private static String scheme;

    public static void loadScheme(boolean useHttps) {
        scheme = useHttps ? "https" : "http";
    }

    public static String getCatalogUrl(String board) {
        return scheme + "://8ch.net/" + board + "/catalog.json";
    }

    public static String getPageUrl(String board, int pageNumber) {
        return scheme + "://8ch.net/" + board + "/" + pageNumber + ".json";
    }

    public static String getThreadUrl(String board, int no) {
        return scheme + "://8ch.net/" + board + "/res/" + no + ".json";
    }

    public static String getImageUrl(String board, String code, String extension, boolean thumb) {
        if (thumb)
            extension = "jpg";
        else
            extension = extension.toLowerCase();
        return scheme + "://8ch.net/" + board + (thumb ? "/thumb/" : "/src/") + code + "." + extension;
    }

    public static String getSpoilerUrl() {
        return scheme + "://8ch.net/static/spoiler.png";
    }

    public static String getCountryFlagUrl(String countryCode) {
        return scheme + "://8ch.net/static/flags/" + countryCode.toLowerCase(Locale.ENGLISH) + ".png";
    }

    // TODO: Remove if unused
    public static String getTrollCountryFlagUrl(String countryCode) {
        return getCountryFlagUrl(countryCode);
    }

    /* TODO: Implement 8chan board manager
    public static String getBoardsUrl() {
        return scheme + "://8chan.co/boards.json";
    }*/

    public static String getReplyUrl() {
        return "https://8ch.net/post.php";
    }

    // TODO: Implement
    public static String getDeleteUrl() {
        return getReplyUrl();
    }

    public static String getBoardUrlDesktop(String board) {
        return scheme + "://8ch.net/" + board + "/";
    }

    public static String getThreadUrlDesktop(String board, int no) {
        return scheme + "://8ch.net/" + board + "/res/" + no + ".html";
    }

    public static String getCatalogUrlDesktop(String board) {
        return scheme + "://8ch.net/" + board + "/catalog.html";
    }

    // FIXME: This url will be valid until around maybe October
    // After this time, we may be using something along the lines of
    // 8ch.net/cp/ for "control panel"
    public static String getModUrl() {
        return scheme + "://8ch.net/mod.php?";
    }

    // FIXME: This may also change as the mod.php interface is changed
    public static String getModDeleteUrl(String board, int no) {
        return getModUrl() + "/" + board + "/delete/" + no;
    }

    // FIXME: This may also change as the mod.php interface is changed
    public static String getModSpoilerUrl(String board, int no) {
        return getModUrl() + "/" + board + "/spoiler_all/" + no;
    }

    // FIXME: This may also change as the mod.php interface is changed
    public static String getModBanUrl(String board, int no) {
        return getModUrl() + "/" + board + "/ban/" + no;
    }

    // TODO: Implement
    public static String getReportUrl(String board, int no) {
        return ""; //return "https://sys.4chan.org/" + board + "/imgboard.php?mode=report&no=" + no;
    }
}
