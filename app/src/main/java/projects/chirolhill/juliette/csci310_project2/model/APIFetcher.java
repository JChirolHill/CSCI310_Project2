package projects.chirolhill.juliette.csci310_project2.model;

public abstract class APIFetcher {
    protected String APIKey;
    protected String endpoint;

    abstract String fetch();
    abstract void parse();
}
