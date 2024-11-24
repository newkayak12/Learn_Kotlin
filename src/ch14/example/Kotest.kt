package ch14.example

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.ints.beOdd
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class Kotest {

    class NumberTest: StringSpec(
        {
            "2 + 2 should be 4" { 2 + 2 shouldBe 4}
            "2 + 2 should be 4" { 2 + 2 shouldBe 4}
            //문자열 { 람다 }
        }
    )

    class NumbersTest2: WordSpec (
        {
            "Addition" When {
                "1 + 2" should {
                    "be equal to 3 " { 1 + 2 shouldBe 3}
                    "be equal to 2 + 1 " { 1 + 2 shouldBe 2 + 1}
                }
            }
        }
    )

    class NumberTest3: FunSpec(
        {
            test("0 should be equal to 0") {0 shouldBe 0}
            context("Arithmetic") {
                context("Addition") {
                    test("2 + 2 shoud be 4") { (2 + 2) shouldBe 4}
                }
                context("Multiplication") {
                    test("2 * 2 shoud be 4") { (2 * 2) shouldBe 4}
                }
            }
        }
    )

    class NumberTest4: DescribeSpec(
        {
            describe("Arithmetic") {
                describe("Addition") {
                    context("1 + 2") {
                        it("should give 3") { 1 + 2 shouldBe  3 }
                    }
                }
                describe("Multiplication") {
                    context("1 * 2") {
                        it("should give 2") { 1 * 2 shouldBe  2 }
                    }
                }
            }
        }
    )

    fun beOdd() = object: Matcher<Int> {
        override fun test(value: Int): MatcherResult {
            return MatcherResult(
                value % 2 != 0,
                "$value shoud be odd",
                "$value should not be odd"
            )
        }
    }

    class NumberTestWithOddMatcher: StringSpec (
        {
            "5 is odd" { 5 should  beOdd() }
        }
    )

    object CustomListener: TestListener {
        override suspend fun beforeTest(testCase: TestCase) {
            println("beforeTest")
        }

        override suspend fun afterSpec(spec: Spec) {
            println("afterSpec")
        }

        override suspend fun beforeSpec(spec: Spec) {
            println("beforeSpec")
        }

        override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            println("afterTest")
        }

        override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
            println("finalizeSpec")
        }

        override suspend fun prepareSpec(kclass: KClass<out Spec>) {
            println("prepareSpec")
        }
    }

    class StringSpecWithConfig: StringSpec (
        {
            "2 + 2 should be 4".config(invocations = 10) { 2 + 2 shouldBe  4}
        }
    )
    object customIsolationMode: AbstractProjectConfig () {
        override val isolationMode: IsolationMode?
            get() = IsolationMode.SingleInstance
    }
}