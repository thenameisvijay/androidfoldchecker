package com.vjcodes.foldable

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.vjcodes.foldablechecklibrary.FoldableDeviceChecker
import com.vjcodes.foldablechecklibrary.FoldableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var foldableDeviceManager: FoldableDeviceChecker
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //foldableDeviceManager = FoldableDeviceChecker(this)

        foldableDeviceManager = FoldableDeviceChecker(this) { isFolded ->
            Log.d("FoldableState Listener", "Device is folded: $isFolded")
        }

        foldableDeviceManager.startListening()

        lifecycleScope.launch {
            if (foldableDeviceManager.isFoldableDevice()) {
                // Device is foldable
                // Handle foldable device

                foldableDeviceManager.observeFoldState().collect { foldState ->
                    when (foldState) {
                        FoldableState.FLAT -> Log.d("FoldableState", "Device is flat")
                        FoldableState.HALF_FOLDED -> Log.d("FoldableState", "Device is half-folded")
                        FoldableState.FOLDED -> Log.d("FoldableState", "Device is folded")
                    }

                }
            } else {
                // Device is not foldable
            }
        }
    }
}