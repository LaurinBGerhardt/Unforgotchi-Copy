package com.jlp.unforgotchi.locations

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

//The premade contract for starting an activity to select an image from the gallery does not
//automatically give the app permissions to display the same image upon restart.
//This custom contract is functionally the same, but also adds all necessary permissions
class RetreiveImageContract : ActivityResultContract<String, Uri?>() {
    @CallSuper
    override fun createIntent(context: Context, input: String): Intent {
        val intent =  Intent(Intent.ACTION_OPEN_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType(input)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return  if (intent == null || resultCode != Activity.RESULT_OK) null
                else intent.data
    }
}
