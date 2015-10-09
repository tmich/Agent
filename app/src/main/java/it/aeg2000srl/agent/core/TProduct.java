package it.aeg2000srl.agent.core;

import com.orm.SugarRecord;

/**
 * Created by tiziano.michelessi on 09/10/2015.
 */
public class TProduct extends SugarRecord<TProduct> {
    public String code;
    public String name;
    public double price;
}
