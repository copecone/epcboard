object Dependency {
    object Kotlin {
        const val VERSION = "2.0.0"
    }

    object Coroutines {
        const val VERSION = "1.7.3"
    }

    object Serialization {
        const val VERSION = Dependency.Kotlin.VERSION
        object Json { const val VERSION = "1.7.1" }
    }

    object Datetime {
        const val VERSION = "0.6.0"
    }
}