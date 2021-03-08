package com.wzy.lamanpro.utils;

import android.graphics.pdf.PdfDocument;
import android.os.Build;

import androidx.annotation.RequiresApi;

import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class PdfManager {

    private static PdfDocument document = new PdfDocument();//1, 建立PdfDocument


    private static void makeViewPdf(View view) {
        if (document == null)
            document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                view.getMeasuredWidth(), view.getMeasuredHeight(), 1).create();//2
        PdfDocument.Page page;
        try {
            page = document.startPage(pageInfo);

        }catch (IllegalStateException e){
            document = new PdfDocument();
            page = document.startPage(pageInfo);
        }
        view.draw(page.getCanvas());//3
        document.finishPage(page);//4

    }

    public static void makeViewEveryPdf(View[] views, String path) {
        for (int i = 0; i < views.length; i++) {
            makeViewPdf(views[i]);
        }
//        String path = Environment.getExternalStorageDirectory() + File.separator + "table.pdf";
        try {
            File e = new File(path);
            if (e.exists()) {
                e.delete();
            }
            document.writeTo(new FileOutputStream(e));
            document.close();//5
//            document.;

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
