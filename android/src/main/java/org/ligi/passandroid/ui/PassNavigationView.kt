package org.ligi.passandroid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.material.navigation.NavigationView
import android.util.AttributeSet
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.navigation_drawer_header.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.ligi.passandroid.App
import org.ligi.passandroid.R
import org.ligi.passandroid.events.PassStoreChangeEvent
import org.ligi.passandroid.model.PassStore

class PassNavigationView(context: Context, attrs: AttributeSet) : NavigationView(context, attrs) {

    val passStore: PassStore = App.kodein.instance()
    val bus: EventBus = App.kodein.instance()

    private fun getIntent(id: Int) = when (id) {
        R.id.menu_settings -> Intent(context, PreferenceActivity::class.java)
        R.id.menu_github -> intentFromUrl("https://github.com/ligi/PassAndroid")
        R.id.menu_beta -> intentFromUrl("https://play.google.com/apps/testing/org.ligi.passandroid")
        R.id.menu_language -> intentFromUrl("https://transifex.com/projects/p/passandroid")
        R.id.menu_share -> Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, marketUrl)
            type = "text/plain"
        }
        else -> null
    }

    private fun intentFromUrl(url: String) = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }

    @SuppressLint("RestrictedApi") // FIXME: temporary workaround for false-positive
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        bus.register(this)

        setNavigationItemSelectedListener { item ->
            getIntent(item.itemId)?.let {
                context.startActivity(it)
                true
            } ?: false
        }

        onPassStoreChangeEvent(PassStoreChangeEvent)
    }

    @SuppressLint("RestrictedApi") // FIXME: temporary workaround for false-positive
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bus.unregister(this)
    }

    private val marketUrl by lazy { context.getString(R.string.market_url, context.packageName) }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPassStoreChangeEvent(@Suppress("UNUSED_PARAMETER") passStoreChangeEvent: PassStoreChangeEvent) {

        val passCount = passStore.passMap.size
        getHeaderView(0).pass_count_header.text = context.getString(R.string.passes_nav, passCount)

        val topicCount = passStore.classifier.getTopics().size
        getHeaderView(0).topic_count_header.text = context.getString(R.string.categories_nav, topicCount)

    }
}
