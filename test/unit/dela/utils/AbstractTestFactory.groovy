package dela.utils

/**
 * @author vedi
 * date 06.12.10
 * time 6:43
 */
abstract class AbstractTestFactory {

    protected def applyParams(domain, params = [:]) {
        if (params) {
            domain.properties = params
        }
    }

}
