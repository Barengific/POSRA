package com.barengific.posra

import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.barengific.posra.EditTextExtension;
import java.lang.ref.WeakReference

class EditTextKeyboardLifecycleObserver(
    private val editText: WeakReference<EditText>
) :
    LifecycleObserver {

    @OnLifecycleEvent(
        Lifecycle.Event.ON_RESUME
    )
    fun openKeyboard() {
//        editText.get()?.postDelayed({ editText.get()?.showKeyboard() }, 50)
    }
    fun hideKeyboard() {
//        editText.get()?.postDelayed({ editText. .get()?.hideKeyboard() }, 50)
    }
}