package com.vibhorsrv.cameraids.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.net.URLConnection;

public class Share {
    public static void shareUri(Context context, Uri uri, String extraText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        intent.setType(URLConnection.guessContentTypeFromName(uri.toString()));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, null));
    }
}
