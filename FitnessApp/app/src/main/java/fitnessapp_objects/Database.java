package fitnessapp_objects;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.auth.User;

import java.util.*;

// All the firebase calling action will stay in this class, Singleton class
public class Database {

    private static Database dataBase_instance = null;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "Database";
    private FirebaseAuth mAuth;
    private UserAccount userAccount;


    private Database(){

        mAuth = FirebaseAuth.getInstance();
        userAccount = UserAccount.getInstance();
    }

    public static Database getInstance(){

        if (dataBase_instance == null) dataBase_instance = new Database();
        return dataBase_instance;

    }

    /**
     *
     * update the firestore with the local user account
     *
     * @param handler: implement and pass this handler if the calling class wants to navigate
     *               to some other screen immediately after the update completes. If not, then
     *               just pass null.
     * @return
     */
    public boolean updateUserAccount(FirestoreCompletionHandler handler){

        FirebaseUser user = mAuth.getCurrentUser();

        db.collection("users").
                document(user.getUid())
                .set(userAccount.getFirestoreUserMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        if(handler!=null){
                            handler.updateUI(true);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        if(handler!=null){
                            handler.updateUI(false);
                        }

                    }
                });

        return true;
    }


    public boolean updateChallengeRoom(ChallengeRoom room, FirestoreCompletionHandler handler){

        FirebaseUser user = mAuth.getCurrentUser();
        // Get a new write batch
        WriteBatch batch = db.batch();

        DocumentReference challengeRef = db.collection("challenges").document();

        // add the new challenge room data to firestore first
        batch.set(challengeRef, room.getFirestoreChallengeRoomMap());

        DocumentReference userAccountRef = db.collection("users").document(user.getUid());

        // update the current user to have this new challenge room
        batch.update(userAccountRef, "challengesJoined", FieldValue.arrayUnion(challengeRef.getId()));

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                handler.updateUI(true);
            }
        });


        return true;

    }


    /**
     *
     * update the local user account with what is stored on firestore
     *
     * @param uid: the uid of the current user
     * @param handler: implement and pass this handler if the calling class wants to navigate
     *                 to some other screen immediately after the update completes. If not, then
     *                 just pass null.
     * @return
     */
    public boolean updateLocalUserAccount(String uid, FirestoreCompletionHandler handler){

        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        userAccount.setUserID(uid);
                        userAccount.setName(document.get("name").toString());
                        userAccount.setEmail(document.get("email").toString());

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        handler.updateUI(true);
                    } else {
                        Log.d(TAG, "No such document");
                        handler.updateUI(false);
                    }

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    handler.updateUI(false);
                }
            }
        });

        return false;
    }

    public boolean getChallengeRoom(String roomID){

        return false;
    }



}
