/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.feed;

import com.androidapp.instasocial.utils.AspectRatio;

public class MediaDetail {
        public MediaDetail(){

        }
        public String media_id;
        public String media_name = "";
        public String media_image = "";

        public String media_size = "";
        public String media_mime_type = "";
        public String media_extension = "";
        public String media_type = "";

        private int mediaWidth = 1;
        private int mediaHeight = 1;

        public int getMediaWidth() {
                return mediaWidth;
        }

        public void setMediaWidth(int mediaWidth) {
                this.mediaWidth = mediaWidth;
        }

        public void setMediaWidth(String mediaWidth) {
                try {
                        this.mediaWidth = Integer.parseInt(mediaWidth);
                } catch (Exception e) {

                }
        }

        public int getMediaHeight() {
                return mediaHeight;
        }

        public void setMediaHeight(int mediaHeight) {
                this.mediaHeight = mediaHeight;
        }

        public void setMediaHeight(String mediaHeight) {
                try {
                        this.mediaHeight = Integer.parseInt(mediaHeight);
                } catch (Exception e) {

                }
        }

        public AspectRatio getMediaAspectRatio(){
                AspectRatio ratio=null;
                if (mediaWidth!=1 || mediaHeight!=1 ){
                        ratio=new AspectRatio(mediaWidth,mediaHeight);
                }
                return ratio;
        }


}
