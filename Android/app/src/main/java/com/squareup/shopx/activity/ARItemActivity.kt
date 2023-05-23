package com.squareup.shopx.activity

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene.OnUpdateListener
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.BaseTransformableNode
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.peceoqicka.x.gallerylayoutmanager.GalleryLayoutManager
import com.peceoqicka.x.gallerylayoutmanager.OnScrollListener
import com.peceoqicka.x.gallerylayoutmanager.SimpleViewTransformListener
import com.squareup.shopx.R
import com.squareup.shopx.adapter.ARItemListAdapter
import com.squareup.shopx.model.AllMerchantsResponse.ShopXMerchant
import com.squareup.shopx.model.GetMerchantDetailResponse
import com.squareup.shopx.model.GetMerchantDetailResponse.Item
import java.io.File
import java.io.IOException


class ARItemActivity : AppCompatActivity() {
    private val TAG = "ARItemActivity"
    private lateinit var back: ImageView
    private lateinit var cart: ImageView
    private lateinit var addToCart: LinearLayout
    private lateinit var instruction: TextView
    private lateinit var arItemList: RecyclerView
    private var items: GetMerchantDetailResponse? = null
    private var arFragment: ArFragment? = null
    private var currentPosition = 0
    var arItems = ArrayList<Item>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)
        FirebaseApp.initializeApp(this)

        back = findViewById(R.id.back)
        back.setOnClickListener { finish() }
        cart = findViewById(R.id.cart)
        addToCart = findViewById(R.id.cart_info)
        instruction = findViewById(R.id.instruction)
        arItemList = findViewById(R.id.ar_item_list)

        currentPosition = intent.extras?.getInt("startPosition") ?: 0
        val merchantInfo = intent.extras?.getSerializable("merchant") as ShopXMerchant
        items = intent.extras?.getSerializable("items") as GetMerchantDetailResponse


        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment?

        arFragment?.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

            for (node in arFragment!!.arSceneView.scene.children) {
                if (node.name == "AR Item") {
                    // already have an AR item in the scene
                    Log.i(TAG, "already have an AR item in the scene, return.")
                    return@setOnTapArPlaneListener
                }
            }

            placeARItemToScene(hitResult)
            instruction.text = "Well done!"

            Handler().postDelayed({
                instruction.text = "Drag the item to move it around"
                Handler().postDelayed({
                    instruction.text = "Rotate it to the angle you like"
                    Handler().postDelayed({
                        if (isBuildingModel) {
                            instruction.text = "Loading AR Model..."
                        } else {
                            instruction.visibility = View.GONE
                        }
                        canShowLaterLoadingInstruction = true
                    }, 4 * 1000)
                }, 4 * 1000)
            }, 3 * 1000)
            arFragment?.arSceneView?.scene?.removeOnUpdateListener(updateListener)

            // users can only hit the scene for one time
            arFragment?.setOnTapArPlaneListener(null)

        }


        Handler().postDelayed({
            arFragment?.arSceneView?.scene?.addOnUpdateListener(updateListener)
        }, 5 * 1000)



        generateARMenu(items!!, currentPosition, merchantInfo)
    }

    var canShowLaterLoadingInstruction = false
    var hasFoundPlane = false
    var isBuildingModel = false
    val updateListener = object: OnUpdateListener {
        override fun onUpdate(p0: FrameTime?) {
            val frame = arFragment?.arSceneView?.arFrame ?: return
            if (frame.getUpdatedTrackables((Plane::class.java)).isEmpty() && !hasFoundPlane) {
                instruction.text = "Searching for surfaces..."
            } else{
                if (isBuildingModel) {
                    instruction.text = "Loading AR Model..."
                } else {
                    instruction.text = "Ready to go, tap to position"
                }


                hasFoundPlane = true
            }
        }

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

            node.scaleController.minScale = 1.0f
            node.scaleController.maxScale = 1.0000001f
            node.localScale = Vector3(1f, 1f, 1f)

            node.setParent(anchorNode)

            arFragment.arSceneView.scene.addChild(anchorNode)

        }

    }

    private val mOnScrollListener = object : OnScrollListener {
        override fun onIdle(snapViewPosition: Int) {
            currentPosition = snapViewPosition
            loadARItem(arItems[currentPosition].arLink)
        }

        override fun onScrolling(scrollingPercentage: Float, fromPosition: Int, toPosition: Int) {

        }
    }

    private fun generateARMenu(merchantDetail: GetMerchantDetailResponse, startPosition: Int, merchantInfo: ShopXMerchant) {



        arItems = ArrayList()

        for (item in merchantDetail.items) {
            if (item.arLink.isNotEmpty()) {
                arItems.add(item)
            }
        }

        arItemList.layoutManager = GalleryLayoutManager.create {
            itemSpace = 100
            onScrollListener = mOnScrollListener
            viewTransformListener = SimpleViewTransformListener(1.2f, 1.2f)
        }
        arItemAdapter = ARItemListAdapter(arItems, this, merchantInfo)
        arItemList.adapter = arItemAdapter
        arItemList.isEnabled = false

        // load the AR item users select from the detail page
        loadARItem(arItems[startPosition].arLink)

    }
    var arItemAdapter: ARItemListAdapter? = null
    val hideLaterLoadingInstruction = Runnable {
        instruction.visibility = View.GONE
    }

    fun loadARItem(arLink: String) {
        isBuildingModel = true
        Handler().removeCallbacks(hideLaterLoadingInstruction)
        if (canShowLaterLoadingInstruction) {
            instruction.visibility = View.VISIBLE
            instruction.text = "Loading AR Model..."
            (arItemList.layoutManager as GalleryLayoutManager).setCanScroll(false)
            renderable = null
        }
        val storage = FirebaseStorage.getInstance()
        val modelRef = storage.reference.child(arLink)

        try {
            val file = File.createTempFile("pizza", "glb")
            modelRef.getFile(file).addOnSuccessListener {
                items?.let { items ->
                    if (arItems[currentPosition].arLink == arLink) {
                        buildModel(file)
                    }
                }
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
                        isBuildingModel = false
                        if (canShowLaterLoadingInstruction) {
                            (arItemList.layoutManager as GalleryLayoutManager).setCanScroll(true)
                            Handler().post(hideLaterLoadingInstruction)
                        }
                    }
                }
        }
    }
}