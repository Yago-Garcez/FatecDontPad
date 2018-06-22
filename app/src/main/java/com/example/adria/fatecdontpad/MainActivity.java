package com.example.adria.fatecdontpad;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference tagsNewReference;

    private TextInputEditText tagEditText;
    private EditText tagContentEditText;
    private TextView tagLabelTextView;
    private TextView tagTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        tagEditText = findViewById(R.id.tagEditText);
        tagContentEditText = findViewById(R.id.tagContentEditText);
        tagContentEditText.setFocusable(false); //Não permitir edições no EditText com conteúdo da TAG
        tagLabelTextView = findViewById(R.id.tagLabelTextView);
        tagTextView = findViewById(R.id.tagTextView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        tagsNewReference = firebaseDatabase.getReference("tags");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tag = tagEditText.getEditableText().toString(); //tipo final pois precisa ser acessa dentro de uma subclasse
                if(TextUtils.isEmpty(tag)/*tagEditText.getEditableText().length() == 0*/){
                    Toast.makeText(MainActivity.this, getString(R.string.tag_empty), Toast.LENGTH_LONG).show();
                    tagContentEditText.setFocusable(false);
                }
                else{
                    tagTextView.setText(tag);
                    tagEditText.setText("");
                    tagContentEditText.setFocusableInTouchMode(true);


                    //Verifica se já existe a TAG digitada no Firebase
                    final DatabaseReference reference = tagsNewReference.child(tag);//Pega o filho da TAG
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) { //se já existir, pega o texto filho e joga no EditText
                                //Toast.makeText(MainActivity.this, "TAG já existe!!!!", Toast.LENGTH_SHORT).show();
                                dataSnapshot.getChildren();
                                Tag t = dataSnapshot.getValue(Tag.class);
                                tagContentEditText.setText(t.getText());
                            }
                            else { //Se não existir, cria um novo filho pra TAG digitada
                                tagContentEditText.setText(""); //Esvazia o campo
                                //Toast.makeText(MainActivity.this, "Não existe essa TAG ainda", Toast.LENGTH_SHORT).show();
                                Tag t = new Tag();
                                t.setText(""); //Necessario, pois se estiver vazio o filho não é criado
                                reference.setValue(t);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                }
            }
        });


        //Listener de texto, de acordo for digitando texto vai setando esse texto pra Tag criada
        tagContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tag = tagTextView.getText().toString();
                Tag t = new Tag();
                t.setText(s.toString());
                tagsNewReference.child(tag).setValue(t);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        ValueEventListener changeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String tag = tagTextView.getText().toString();
                if(tag.length() > 0){ //Só executa se já existir uma TAG, se não ele sobrescreve o fb com texto vazio do EditText
                    Tag t = dataSnapshot.child(tag).getValue(Tag.class);
                    String newContent = t.getText();
                    //Toast.makeText(MainActivity.this, "TAG atual: "+ tag +".\n Contéudo de t.getText(): " + newContent, Toast.LENGTH_SHORT).show();

                    tagContentEditText.setText(newContent);
                    tagContentEditText.setSelection(tagContentEditText.getText().length());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        tagsNewReference.addValueEventListener(changeListener);
    }//fim onCreate()



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
    }
}
