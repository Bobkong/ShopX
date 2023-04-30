package com.squareup.shopx.activity

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.squareup.shopx.R
import java.io.File
import java.io.IOException


class ARItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_test)
        FirebaseApp.initializeApp(this)


        val storage = FirebaseStorage.getInstance()
        val modelRef = storage.reference.child("pizza.glb")
        val arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment?

        findViewById<View>(R.id.download_button)
            .setOnClickListener {
                try {
                    val file = File.createTempFile("pizza", "glb")
                    modelRef.getFile(file).addOnSuccessListener {
                        buildModel(file)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

        arFragment?.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment.arSceneView.scene)

            val node = TransformableNode(arFragment.transformationSystem)
            node.renderable = renderable

            // Set the min and max scales of the ScaleController.
            // Default min is 0.75, default max is 1.75.

            // Set the min and max scales of the ScaleController.
            // Default min is 0.75, default max is 1.75.
            node.scaleController.minScale = 0.2f
            node.scaleController.maxScale = 2.0f

            // Set the local scale of the node BEFORE setting its parent

            // Set the local scale of the node BEFORE setting its parent
            node.localScale = Vector3(0.4f, 0.4f, 0.4f)

            node.setParent(anchorNode)

            arFragment.arSceneView.scene.addChild(anchorNode)
        }
    }

    private var renderable: ModelRenderable? = null
    private fun buildModel(file: File) {
        val renderableSource = RenderableSource
            .builder()
            .setSource(this, Uri.parse(file.path), RenderableSource.SourceType.GLB)
            .setRecenterMode(RenderableSource.RecenterMode.ROOT)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ModelRenderable.builder()
                .setSource(this, renderableSource)
                .setRegistryId(file.path)
                .build()
                .thenAccept { modelRenderable: ModelRenderable? ->
                    Toast.makeText(
                        this,
                        "Model Built",
                        Toast.LENGTH_SHORT
                    ).show()
                    renderable = modelRenderable
                }
        }
    }
}