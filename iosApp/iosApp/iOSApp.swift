import SwiftUI
import Shared

@main
struct iOSApp: App {
    // Koin has to be running before the first Compose frame resolves a ViewModel. The APOD key is
    // baked into the shared framework by the :shared generateApodApiKey task, so there is nothing
    // to pass in here.
    init() {
        InitKoin_iosKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}