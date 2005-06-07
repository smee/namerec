          package com.biemann.pendel;

          public class Watcher implements Runnable {

            boolean running;
            PendelPanel clickibunti;

            Thread watchThread=null;

            public Watcher(PendelPanel parameter) {
                   clickibunti=parameter;
                   watchThread=new Thread(this);
                   running=true;
                   watchThread.start();

            }

            public void run() {
            try {
              while(running) {
                watchThread.sleep(2000);
                clickibunti.softsupdate();
              }
            } catch (InterruptedException e) {}

            }

            public void stop() {
              if (watchThread!=null) watchThread.stop();
              watchThread=null;
            }


          }