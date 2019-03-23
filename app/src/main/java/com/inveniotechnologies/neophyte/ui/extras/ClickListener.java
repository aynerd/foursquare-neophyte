package com.inveniotechnologies.neophyte.ui.extras;

import android.view.View;

/**
 * Created by winner-timothybolorunduro on 16/06/2017.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
