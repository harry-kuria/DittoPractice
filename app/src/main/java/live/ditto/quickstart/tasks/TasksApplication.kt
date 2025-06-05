package live.ditto.quickstart.tasks

import android.app.Application
import android.content.Context
import live.ditto.Ditto
import live.ditto.DittoIdentity
import live.ditto.DittoLogLevel
import live.ditto.DittoLogger
import live.ditto.android.DefaultAndroidDittoDependencies
import live.ditto.quickstart.tasks.DittoHandler.Companion.ditto
import live.ditto.transports.DittoTransportConfig

class TasksApplication : Application() {

    companion object {
        private var instance: TasksApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    init {
        instance = this
    }
    
    override fun onCreate() {
        super.onCreate()
        setupDitto()
    }

    private fun setupDitto() {
        val androidDependencies = DefaultAndroidDittoDependencies(applicationContext)

        //read values from build.gradle.kts (Module:app) which reads from environment file
        val appId = BuildConfig.DITTO_APP_ID
        val token = BuildConfig.DITTO_PLAYGROUND_TOKEN
        val authUrl = BuildConfig.DITTO_AUTH_URL
        val webSocketURL = BuildConfig.DITTO_WEBSOCKET_URL

        val enableDittoCloudSync = false

        /*
         *  Setup Ditto Identity
         *  https://docs.ditto.live/sdk/latest/install-guides/kotlin#integrating-and-initializing
         */
        val identity = DittoIdentity.OnlinePlayground(
            dependencies = androidDependencies,
            appId = appId,
            token = token,
            customAuthUrl = authUrl,
            enableDittoCloudSync = enableDittoCloudSync // This is required to be set to false to use the correct URLs
        )

        ditto = Ditto(androidDependencies, identity)
        ditto.updateTransportConfig { config ->
            // Set the Ditto Websocket URL
            config.connect.websocketUrls.add(webSocketURL)
        }

        // disable sync with v3 peers, required for DQL
        ditto.disableSyncWithV3()
    }
}
