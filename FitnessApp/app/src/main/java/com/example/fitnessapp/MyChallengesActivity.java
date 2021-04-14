package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import fitnessapp_objects.ChallengeRoom;
import fitnessapp_objects.ChallengeRoomModel;
import fitnessapp_objects.Database;

public class MyChallengesActivity extends AppCompatActivity implements Database.OnRoomGetCompletionHandler, AdapterView.OnItemClickListener {

    ListView myChallengesLV;
    ArrayList<ChallengeRoomModel> challengeRoomModelArrayList;
    ChallengeRoomLVAdapter adapter;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_challenges);

        myChallengesLV = (ListView) findViewById(R.id.my_chall_lv);

        challengeRoomModelArrayList = new ArrayList<>();
        adapter = new ChallengeRoomLVAdapter(this, challengeRoomModelArrayList);
        myChallengesLV.setAdapter(adapter);
        myChallengesLV.setOnItemClickListener(this);
        db = Database.getInstance();
        db.getMyChallenges(this);

    }

    public void reload(View view){

        db.getMyChallenges(this);

    }

    @Override
    public void challengeRoomsTransfer(Map<String, ChallengeRoom> rooms) {

        challengeRoomModelArrayList.clear();

        for(Map.Entry<String,ChallengeRoom> entry : rooms.entrySet()){

            ChallengeRoom room = entry.getValue();

            ChallengeRoomModel model = new ChallengeRoomModel(entry.getKey(), room.getName(),room.getType(),room.isBet(), room.getBetAmount(),room.getEndDate(), room.getPassword());
            challengeRoomModelArrayList.add(model);

        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ChallengeRoomModel challengeRoomModel = challengeRoomModelArrayList.get(position);

        Intent intent = new Intent(this, ChallengeLobbyActivity.class);
        intent.putExtra("roomID", challengeRoomModel.getId());
        startActivity(intent);

    }
}