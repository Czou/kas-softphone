/*
Softphone application for Android. It can make video calls using SIP with different video formats and audio formats.
Copyright (C) 2011 Tikal Technologies

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3
as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kurento.kas.phone.preferences;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.kurento.kas.phone.softphone.R;
import com.kurento.kas.sip.ua.Preferences;

public class Connection_Preferences extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private static boolean preferenceConnectionChanged = false;
	private static String info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreferenceChanged(false);
		addPreferencesFromResource(R.layout.connection_preferences);
		this.getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	public static Map<String, String> getConnectionPreferences(Context context) {
		Map<String, String> params = new HashMap<String, String>();
		String username, password, domain, ip;

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);

		username = settings.getString(Keys_Preferences.SIP_LOCAL_USERNAME, "");
		password = settings.getString(Keys_Preferences.SIP_LOCAL_PASSWORD, "");
		domain = settings.getString(Keys_Preferences.SIP_LOCAL_DOMAIN, "");

		ip = settings.getString(Preferences.SIP_PROXY_SERVER_ADDRESS, "");

		if (username.equals("") || domain.equals("") || ip.equals(""))
			return null;

		params.put(Keys_Preferences.SIP_LOCAL_USERNAME,
				username.replace(" ", ""));
		params.put(Keys_Preferences.SIP_LOCAL_PASSWORD,
				password.replace(" ", ""));
		params.put(Keys_Preferences.SIP_LOCAL_DOMAIN, domain.replace(" ", ""));

		params.put(Preferences.SIP_PROXY_SERVER_ADDRESS, ip.replace(" ", ""));

		return params;
	}

	public static String getConnectionPreferencesInfo(Context context) {
		Map<String, String> params = getConnectionPreferences(context);
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);
		int port = settings.getInt(Preferences.SIP_PROXY_SERVER_PORT, -1);

		if (params != null) {
			info = "Connection preferences\n" + "User: \n"
					+ params.get(Keys_Preferences.SIP_LOCAL_USERNAME) + "@"
					+ params.get(Keys_Preferences.SIP_LOCAL_DOMAIN)
					+ "\n\nProxy: \n"
					+ params.get(Preferences.SIP_PROXY_SERVER_ADDRESS) + ":"
					+ port;
		} else {
			info = "Connection preferences are incorrect";
		}
		return info;
	}

	public static String getConnectionNetPreferenceInfo(Context context) {
		Map<String, Object> params = getConnectionNetPreferences(context);
		if (params != null) {
			info = "Connection Net Preference\n"
					+ "Keep Alive\n"
					+ (Boolean) params
							.get(Keys_Preferences.MEDIA_NET_KEEP_ALIVE)
					+ "\n\nKeep Delay (seg)\n"
					+ (Long) params.get(Keys_Preferences.MEDIA_NET_KEEP_DELAY)
					+ "\n\nTransport\n"
					+ (String) params.get(Keys_Preferences.MEDIA_NET_TRANSPORT);
		} else {
			info = "Connection net preferences are incorrect";
		}
		return info;
	}

	public static Map<String, Object> getConnectionNetPreferences(
			Context context) {
		Map<String, Object> params = new HashMap<String, Object>();

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);

		params.put(Keys_Preferences.MEDIA_NET_KEEP_ALIVE, settings.getBoolean(
				Keys_Preferences.MEDIA_NET_KEEP_ALIVE, false));
		params.put(Keys_Preferences.MEDIA_NET_KEEP_DELAY, Long
				.parseLong(settings.getString(
						Keys_Preferences.MEDIA_NET_KEEP_DELAY, "10")));
		params.put(Keys_Preferences.MEDIA_NET_TRANSPORT,
				settings.getString(Keys_Preferences.MEDIA_NET_TRANSPORT, "UDP"));

		return params;
	}

	public static Map<String, String> getStunPreferences(Context context) {
		Map<String, String> params = new HashMap<String, String>();
		String stunHost, stunPort;

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);

		Boolean stunEnable = settings.getBoolean(Keys_Preferences.STUN_ENABLE,
				false);

		if (stunEnable) {
			stunHost = settings.getString(Keys_Preferences.STUN_LIST,
					"193.147.51.24");
			stunPort = "3478";
		} else {
			stunHost = "";
			stunPort = "0";
		}

		Log.d("Connection_Preferences", "StunHost = " + stunHost + ":"
				+ stunPort);
		params.put(Keys_Preferences.STUN_HOST, stunHost);
		params.put(Keys_Preferences.STUN_HOST_PORT, stunPort);

		return params;
	}

	private synchronized static void setPreferenceChanged(boolean hasChanged) {
		preferenceConnectionChanged = hasChanged;
	}

	public static synchronized boolean isPreferenceChanged() {
		return preferenceConnectionChanged;
	}

	public static void resetChanged() {
		setPreferenceChanged(false);
	}

	@Override
	protected void onDestroy() {
		this.getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.d(this.getClass().getName(), "Connection Preferecnces Changed : "
				+ sharedPreferences + " Key " + key);
		if (key.equals(Keys_Preferences.SIP_LOCAL_DOMAIN)
				|| key.equals(Keys_Preferences.SIP_LOCAL_PASSWORD)
				|| key.equals(Keys_Preferences.SIP_LOCAL_USERNAME)
				|| key.equals(Preferences.SIP_PROXY_SERVER_ADDRESS)
				|| key.equals(Preferences.SIP_PROXY_SERVER_PORT)
				|| key.equals(Keys_Preferences.MEDIA_NET_KEEP_ALIVE)
				|| key.equals(Keys_Preferences.MEDIA_NET_KEEP_DELAY)
				|| key.equals(Keys_Preferences.MEDIA_NET_TRANSPORT)
				|| key.equals(Keys_Preferences.STUN_ENABLE)
				|| key.equals(Keys_Preferences.STUN_LIST))
			setPreferenceChanged(true);
		else
			setPreferenceChanged(false);
	}
}
