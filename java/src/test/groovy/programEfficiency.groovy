import efficiency.Concurrent
import efficiency.IO
import spock.lang.Specification
import efficiency.AlgComplexity
import spock.lang.Unroll

class programEfficiency extends Specification {

    IO io = new IO()

    @Unroll
    def "algComplexityCost test"() {
        when:
        long quadXSpendTime = AlgComplexity.calQuadXSpendTime(x)
        long xSpendTime = AlgComplexity.calXSpendTime(x)
        long logXSpendTime = AlgComplexity.calLogXSpendTime(x)

        then:
        println("quadXSpendTime: " + quadXSpendTime)
        println("xSpendTime: " + xSpendTime)
        println("logXSpendTime: " + logXSpendTime)

        where:
        x       | _
        10000L  | _
        100000L | _
    }

    def "ioCost test"() {
        when:
        long onceCost = io.calOnceIOCost(n)
        long multiCost = io.calMultiIOCost(n)

        then:
        println(onceCost)
        println(multiCost)

        where:
        n    | _
        10   | _
        100  | _
        1000 | _
    }

    def "concurrentCost test"() {
        when:
        long singleThreadCost = Concurrent.calSingleThreadCost(n)
        long multiThreadCost = Concurrent.calMultiThreadCost(n)

        then:
        println(singleThreadCost)
        println(multiThreadCost)

        where:
        n     | _
        100   | _
        1000  | _
        10000 | _
    }
}