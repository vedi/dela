package dela.grails

import dela.Setup

class StoreService {

    static transactional = true
    static scope = "session"

    def Setup setup
}
