package org.example.model;

import java.util.ArrayList;

public class Piece {
    private String id;
    private Player player;
    private Position currentPosition;
    private Boolean isComplete;
    private Boolean isOwnhand;
    private ArrayList<Piece> stackPieces;

    //생성자
    public Piece(String id, Player player) {
        this.id = id;
        this.player = player;
        this.currentPosition = null;
        this.isComplete = false;
        this.isOwnhand = true;
        this.stackPieces = new ArrayList<Piece>();
    }

    // 말 움직임

    // 말 업기

    // 말 잡기

    //말 꺼내기

    //말 골인


}
