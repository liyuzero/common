package com.yu.lib.common.bundles.monitor;

import android.content.Intent;

/**
 * Created by liyu on 2017/12/11.
 */

public interface MAEActivityResultListener {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
