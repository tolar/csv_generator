import play.api.mvc.WithFilters


object Global extends WithFilters(Filters.SessionIdFilter);

