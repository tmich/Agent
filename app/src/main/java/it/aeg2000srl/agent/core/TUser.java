package it.aeg2000srl.agent.core;

import com.orm.SugarRecord;

/**
 * Created by tiziano.michelessi on 09/10/2015.
 */
public class TUser extends SugarRecord<TUser> {
    String username;
    String password;
    boolean locked;
}
