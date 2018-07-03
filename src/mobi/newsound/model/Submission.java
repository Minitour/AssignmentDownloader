package mobi.newsound.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Antonio Zaitoun on 02/07/2018.
 */
public class Submission {

    @Expose
    private int id;

    @Expose
    private int userid;

    @Expose
    private long timemodified;

    @Expose
    private String status;

    @Expose
    private Plugin[] plugins;

    public static class Plugin {
        @Expose
        private String type;

        @Expose
        private String name;

        @Expose
        private FilesArea[] fileareas;


        public static class FilesArea {

            @Expose
            private String area;

            @Expose
            private File[] files;

            public static class File {
                @Expose
                private String filename;

                @Expose
                private String filesize;

                @Expose
                private String fileurl;

                @Expose
                private long timemodified;

                @Expose
                private String mimetype;

                public String getFilename() {
                    return filename;
                }

                public String getFilesize() {
                    return filesize;
                }

                public String getFileurl() {
                    return fileurl;
                }

                public long getTimemodified() {
                    return timemodified;
                }

                public String getMimetype() {
                    return mimetype;
                }

                public void setFilename(String filename) {
                    this.filename = filename;
                }

                public void setFilesize(String filesize) {
                    this.filesize = filesize;
                }

                public void setFileurl(String fileurl) {
                    this.fileurl = fileurl;
                }

                public void setTimemodified(long timemodified) {
                    this.timemodified = timemodified;
                }

                public void setMimetype(String mimetype) {
                    this.mimetype = mimetype;
                }
            }

            public String getArea() {
                return area;
            }

            public File[] getFiles() {
                return files;
            }

            public void setArea(String area) {
                this.area = area;
            }

            public void setFiles(File[] files) {
                this.files = files;
            }
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public FilesArea[] getFileareas() {
            return fileareas;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setFileareas(FilesArea[] fileareas) {
            this.fileareas = fileareas;
        }
    }

    public int getId() {
        return id;
    }

    public int getUserid() {
        return userid;
    }

    public long getTimemodified() {
        return timemodified;
    }

    public String getStatus() {
        return status;
    }

    public Plugin[] getPlugins() {
        return plugins;
    }

    private Plugin.FilesArea.File getPrimaryFile(){
        try {
            return plugins[0].fileareas[0].files[0];
        }catch (Exception e){ return null; }
    }

    public String getFileName(){
        try { return getPrimaryFile().filename; }
        catch (Exception e) { return "null";}
    }

    public String getFileURL() {
        try { return getPrimaryFile().fileurl; }
        catch (Exception e) { return "null";}
    }

    public long getTimeModified() {
        try { return getPrimaryFile().timemodified; }
        catch (Exception e) { return 0;}
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setTimemodified(long timemodified) {
        this.timemodified = timemodified;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPlugins(Plugin[] plugins) {
        this.plugins = plugins;
    }
}
