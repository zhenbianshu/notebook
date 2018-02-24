import spock.lang.Specification
import efficiency.AlgComplexity
import spock.lang.Unroll

class AlgComplexityTest extends Specification {

    @Unroll
    def "test"() {
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
}