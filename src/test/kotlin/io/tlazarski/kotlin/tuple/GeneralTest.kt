package io.tlazarski.kotlin.tuple

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GeneralTest {
    @Test
    fun destructuringTest() {
        val tuple2 = Tuple2("a", "b")
        val (_1, _2) = tuple2
        assertThat(_1).isEqualTo("a")
        assertThat(_2).isEqualTo("b")
    }

    @Test
    fun covarianceTest() {
        val tuple2: Tuple2<Any, Any> = Tuple2("a", "b")
    }
}
