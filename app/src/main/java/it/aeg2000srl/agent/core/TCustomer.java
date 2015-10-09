package it.aeg2000srl.agent.core;

import com.orm.SugarRecord;

/**
 * Created by tiziano.michelessi on 09/10/2015.
 */
public class TCustomer extends SugarRecord<TCustomer> {
    public String code;
    public String name;
    public String address;
    public String cap;
    public String city;
    public String province;
    public String telephone;
    public String iva;

    @Override
    public String toString() {
        return name;
    }
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TCustomer)) {
            return false;
        }

        TCustomer otherCustomer = (TCustomer)other;
        return this.code == otherCustomer.code;
    }
}
