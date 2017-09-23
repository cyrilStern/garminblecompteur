package com.example.root.garminblecompteur.bluethoofRepo;

import android.content.Intent;

/**
 * Created by cyrilstern1 on 09/09/2017.
 */

interface IBleBroadcst {
    void speedCadenceAction(Intent intent);
    void heartRateAction(Intent intent);

    void crankRateAction(Intent intent);

}
