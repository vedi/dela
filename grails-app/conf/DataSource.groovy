dataSource {
	pooled = true
    dbCreate = "update"
	driverClassName = "com.mysql.jdbc.Driver"
	username = "root"
	password = ""
    url="jdbc:mysql://localhost:3306/dela?useUnicode=true"
}
hibernate {
//    current_session_context_class='thread'

    cache.use_second_level_cache=true
    cache.use_query_cache=true
    cache.provider_class='net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
	development {
		dataSource {
		}
	}
	test {
		dataSource {
		}
	}
	production {
		dataSource {
		}
	}
}