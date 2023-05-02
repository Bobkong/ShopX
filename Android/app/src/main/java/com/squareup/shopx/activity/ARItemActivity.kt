package com.squareup.shopx.activity

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.squareup.shopx.R
import com.squareup.shopx.adapter.ARItemListAdapter
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.model.GetMerchantDetailResponse
import com.squareup.shopx.model.GetMerchantDetailResponse.Item
import java.io.File
import java.io.IOException


class ARItemActivity : AppCompatActivity() {
    private val TAG = "ARItemActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)
        FirebaseApp.initializeApp(this)

        val arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment?
        arFragment?.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

            for (node in arFragment.arSceneView.scene.children) {
                if (node.name == "AR Item") {
                    // already have an AR item in the scene
                    Log.i(TAG, "already have an AR item in the scene, return.")
                    return@setOnTapArPlaneListener
                }
            }

            placeARItemToScene(hitResult)

            // users can only hit the scene for one time
            arFragment.setOnTapArPlaneListener(null)

        }

        generateARMenu()
    }

    private fun placeARItemToScene(hitResult: HitResult?) {

        val arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment?
        arFragment?.let {


            if (hitResult == null) {
                // from replacing
                for (node in arFragment.arSceneView.scene.children) {
                    if (node.name == "AR Item") {
                        // remove the node
                        if (node?.children?.size != 0){
                            node.removeChild(node.children[0])
                        }

                        val transformableNode = TransformableNode(arFragment.transformationSystem)
                        transformableNode.renderable = renderable
                        transformableNode.setParent(node)
                    }
                }
                return
            }

            // from clicking
            var anchorNode: AnchorNode? = null
            val anchor = hitResult.createAnchor()
            anchorNode = AnchorNode(anchor)

            anchorNode.setParent(arFragment.arSceneView.scene)
            anchorNode.name = "AR Item"

            val node = TransformableNode(arFragment.transformationSystem)
            node.renderable = renderable

            // Set the min and max scales of the ScaleController.
            // Default min is 0.75, default max is 1.75.

            // Set the min and max scales of the ScaleController.
            // Default min is 0.75, default max is 1.75.
            node.scaleController.minScale = 0.5f
            node.scaleController.maxScale = 2.0f

            // Set the local scale of the node BEFORE setting its parent

            // Set the local scale of the node BEFORE setting its parent
            node.localScale = Vector3(1f, 1f, 1f)

            node.setParent(anchorNode)

            arFragment.arSceneView.scene.addChild(anchorNode)

        }

    }

    private fun generateARMenu() {
        val merchantDetail = intent.extras?.get("merchantDetail") as GetMerchantDetailResponse

        val arItemList = findViewById<RecyclerView>(R.id.ar_item_list)
        arItemList?.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val arItems = ArrayList<Item>()

        for (item in merchantDetail.items) {
            if (item.arLink.isNotEmpty()) {
                arItems.add(item)
            }
        }

        arItemList.adapter = ARItemListAdapter(arItems, this)

        // load the AR item users select from the detail page
        val itemId = intent.extras?.get("itemId") as String
        for (item in arItems) {
            if (item.itemId == itemId) {
                loadARItem(item.arLink)
            }
        }

    }

    fun loadARItem(arLink: String) {

        val storage = FirebaseStorage.getInstance()
        val modelRef = storage.reference.child(arLink)

        try {
            val file = File.createTempFile("pizza", "glb")
            modelRef.getFile(file).addOnSuccessListener {
                buildModel(file)
            }
        } catch (e: IOException) {
            e.printStackTrace()
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
                    renderable = modelRenderable

                    val arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment?
                    if (arFragment?.arSceneView?.scene?.children?.size != 0) {
                        // already have an AR item, just replace it
                        placeARItemToScene(null)
                    }
                }
        }
    }
}