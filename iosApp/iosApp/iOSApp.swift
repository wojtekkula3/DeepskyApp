import SwiftUI
import Shared

@main
struct iOSApp: App {
    // Koin has to be running before the first Compose frame resolves a ViewModel, and before the repair resolves
    // its dependencies.
    init() {
        InitKoin_iosKt.doInitKoin()
        LegacyFavouritesRepairKt.startLegacyFavouritesRepair()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}