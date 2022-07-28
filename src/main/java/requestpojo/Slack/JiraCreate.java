package requestpojo.Slack;

public class JiraCreate {

    public Fields fields;

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public static class Project{
        public int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class Issuetype{
        public int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class Fields{
        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Issuetype getIssuetype() {
            return issuetype;
        }

        public void setIssuetype(Issuetype issuetype) {
            this.issuetype = issuetype;
        }

        public Project project;
        public String summary;
        public String description;
        public Issuetype issuetype;
        public Reporter reporter;

        public Reporter getReporter() {
            return reporter;
        }
        public static class Reporter{
            public String name ="";

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public void setReporter(Reporter reporter) {
            this.reporter = reporter;
        }
    }

}

