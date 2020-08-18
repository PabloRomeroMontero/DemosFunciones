package pabloromeromontero.r.demosfunciones.statusBar

import android.view.WindowManager
import androidx.core.content.ContextCompat
import pabloromeromontero.r.demosfunciones.R

fun changeColorStatusBar() {
    // clear FLAG_TRANSLUCENT_STATUS flag:
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

    // finally change the color
    window.statusBarColor = ContextCompat.getColor(this, R.color.rojo_cocacola)
}