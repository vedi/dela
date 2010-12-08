package dela.context

/**
 * @author vedi
 * date 10.09.2010
 * time 10:26:20
 */
class DataContext {

    def sessionContext
    def domainClass

    def getAccount() {
        sessionContext.account
    }

    def setAccount(account) {
        sessionContext.account = account
    }

    def getSetup() {
        sessionContext.setup
    }

    def setSetup(setup) {
        sessionContext.setup = setup
    }

    def setMetaProvider(metaProvider) {
        sessionContext.metaProvider = metaProvider
    }

    def getStoreService() {
        sessionContext.storeService
    }
}
