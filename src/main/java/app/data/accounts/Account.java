package app.data.accounts;

import java.io.Serializable;
import java.util.Objects;

public class Account implements Serializable
{
    private static final long serialVersionUID = 1992L;
    private final String name;
    private final String serwer;
    private final String web;

    public Account(String name, String serwer, String web) {
        this.name = name;
        this.serwer = serwer;
        this.web = web;
    }

    public String getName() {
        return name;
    }

    public String getSerwer() {
        return serwer;
    }

    public String getWeb() {
        return web;
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, serwer, web);
    }

    @Override
    public boolean equals(Object obj) {
        Account a = (Account) obj;

        return this.name.equals(a.getName()) && this.serwer.equals(a.getSerwer());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}