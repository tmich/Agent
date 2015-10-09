package it.aeg2000srl.agent;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import it.aeg2000srl.agent.core.TCustomer;
import utils.MyStreamReader;

public class MainActivity extends AppCompatActivity {

    ProgressDialog barProgressDialog;
    Handler updateBarHandler;
    ListView customersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customersList = (ListView)findViewById(R.id.listCustomers);
        customersList.setEmptyView(findViewById(R.id.empty_list));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        customersList.setAdapter(new ArrayAdapter<TCustomer>(this, android.R.layout.simple_list_item_1, TCustomer.find(TCustomer.class, null, null)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void updateFromWs() {
        barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle(getString(R.string.title_activity_update_customers));
        barProgressDialog.setMessage(getString(R.string.please_wait));
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.show();
        updateBarHandler = new Handler();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String url = SP.getString("api_address", getString(R.string.test_url)) + "/customers";
        new DownloadCustomersService(updateBarHandler).execute(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_update) {
            updateFromWs();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected ArrayAdapter<TCustomer> getCustomersAdapter() {
        return (ArrayAdapter<TCustomer>) customersList.getAdapter();
    }


    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    /***********************************************************************************************/
    /****************                           ASNYC TASK                          ****************/
    /***********************************************************************************************/
    class DownloadCustomersService extends AsyncTask<String, Integer, Integer> {
        private String url = null;
        private final int CONN_TIMEOUT = 10000;
        private Exception exception;
        private List<TCustomer> customers;
        private Handler handler;

        public DownloadCustomersService(Handler handler) {
            this.handler = handler;
//            showMessage("Prima - Sono presenti " + repo.size() + " clienti");
        }

        @Override
        protected Integer doInBackground(String... urls) {
            customers = new ArrayList<>();
            this.url = urls[0];

            HttpURLConnection urlConnection = null;
            int sz = 0;

            try {
                // Send GET data request
                URL url = new URL(this.url);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(CONN_TIMEOUT);
                urlConnection.setReadTimeout(20000);
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String Content = MyStreamReader.readStream(in);
                urlConnection.disconnect();

                JSONObject jsonMainNode = new JSONObject(Content);
                JSONArray items = jsonMainNode.getJSONArray("json_list");
                sz = items.length();
//                List<TCustomer> customers = TCustomer.find(TCustomer.class, null, null);

                barProgressDialog.setMax(sz);

                for (int i=0; i < sz; i++) {
                    publishProgress(i + 1);
                    JSONObject obj = items.getJSONObject(i);
                    String code = obj.getString("code");

                    TCustomer customer = new TCustomer();
                    try {
                        customer = TCustomer.find(TCustomer.class, "code = ?", code).get(0);
                    } catch (Exception exc) {
                        //
                    }

                    customer.name  = obj.getString("name");
                    customer.address = obj.has("address") ? obj.getString("address") : "";
                    customer.city = obj.has("city") ?  obj.getString("city") : "";
                    customer.code = obj.getString("code");

                    customers.add(customer);

                }
                TCustomer.saveInTx(customers);
            }
            catch (Exception e) {
                exception = e;
                e.printStackTrace();
            }

            return sz;
        }

        /** This method runs on the UI thread */
        protected void onProgressUpdate(final Integer... progressValue) {
            handler.post(new Runnable() {
                public void run() {
                    barProgressDialog.setProgress(progressValue[0]);
                }
            });
        }

        protected void onPostExecute(Integer result) {
            if(exception == null) {
                getCustomersAdapter().addAll(TCustomer.find(TCustomer.class, null, null));
                getCustomersAdapter().notifyDataSetChanged();
                showMessage(String.valueOf(TCustomer.find(TCustomer.class, null, null).size()));
            } else {
                showError(exception);
            }
            barProgressDialog.dismiss();
        }

        private void showError(Exception e) {
            try {
                throw e;
            }
            catch (IOException ex) {
                showMessage("Errore di connessione");
            }
            catch (JSONException ex) {
                showMessage("Ricevuti dati non validi");
            }
            catch (Exception ex) {
                showMessage("Errore sconosciuto");
            }
        }


    }
}
