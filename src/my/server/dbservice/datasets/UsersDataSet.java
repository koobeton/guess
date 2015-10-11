package my.server.dbservice.datasets;

import my.server.resourcesystem.DBResource;
import my.server.resourcesystem.ResourceFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class UsersDataSet {

	private static final DBResource DB_RESOURCE =
			(DBResource) ResourceFactory.instance().getResource("./data/DBResource.xml");
	private static final int UNAUTHORIZED_ID = DB_RESOURCE.getUnauthorizedId();

    @Id
    @Column(name="id")
	private int id;
    @Column(name="name")
	private String name;

    public UsersDataSet() {
    }

    public UsersDataSet(int id, String name) {
		this.id = id;
		this.name = name;
	}

    public UsersDataSet(String name) {
        this.id = UNAUTHORIZED_ID;
        this.name = name;
    }

    public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}	
}
