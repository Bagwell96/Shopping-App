package com.mertkavrayici.shoppingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mertkavrayici.shoppingapplication.Model.CategoryModel;
import com.mertkavrayici.shoppingapplication.Model.Product;
import com.mertkavrayici.shoppingapplication.Prevalent.Prevalent;
import com.mertkavrayici.shoppingapplication.ViewHolder.CategoryAdapter;
import com.mertkavrayici.shoppingapplication.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    Context mContext;

    private DatabaseReference ProductsRef,fvrtRef,fvrtListRef;
    private RecyclerView recyclerView,categoryRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    private CategoryAdapter categoryAdapter;
    private Boolean fvrtChecker=false;
    private List<CategoryModel> categoryModelList;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    private FirebaseFirestore firebaseFirestore;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products");

        fvrtRef=database.getReference("favorites");
        fvrtListRef=database.getReference("favoritelist").child(Prevalent.currentOnlineUser.getPhone());
        //favRef=FirebaseDatabase.getInstance().getReference().child("Favorites");



        Paper.init(this);

        toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setTitle("Ana Menü");
        setSupportActionBar(toolbar);
        mContext=HomeActivity.this;                                                                //İtemler yüklendi
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);


        View headerView=navigationView.getHeaderView(0);
        TextView userNameTextView=headerView.findViewById(R.id.username);
        CircleImageView profileImageView=headerView.findViewById(R.id.profile_image);

        userNameTextView.setText(Prevalent.currentOnlineUser.getUsername());
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);

        recyclerView=findViewById(R.id.recycler_menu);
        categoryRecyclerView=findViewById(R.id.category_recycler);




       LinearLayoutManager testlayoutManager=new LinearLayoutManager(this);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        testlayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(testlayoutManager);


        categoryModelList=new ArrayList<CategoryModel>();

        categoryAdapter=new CategoryAdapter(categoryModelList);
        categoryRecyclerView.setAdapter(categoryAdapter);


        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Categories").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){

                            for(QueryDocumentSnapshot documentSnapshot:task.getResult()){

                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(),documentSnapshot.get("categoryName").toString()));

                            }
                            categoryAdapter.notifyDataSetChanged();

                        }
                        else{
                            String error=task.getException().getMessage();
                            Toast.makeText(HomeActivity.this,error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });




categoryAdapter=new CategoryAdapter(categoryModelList);
categoryRecyclerView.setAdapter(categoryAdapter);
categoryAdapter.notifyDataSetChanged();






        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() { //flooting actionbutton henüz yazılmadı
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(HomeActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                if(menuItem.isChecked())
                {
                    menuItem.setChecked(false);
                }
                else
                {
                    menuItem.setChecked(true);
                }

                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()){
                    case R.id.nav_search:
                        Intent intent3=new Intent(HomeActivity.this,SearchProductsActivity.class);
                        startActivity(intent3);

                        return true;
                    case R.id.nav_orders:
                        Toast.makeText(mContext, "Second Clicked", Toast.LENGTH_SHORT).show();

                        return true;

                    case R.id.nav_category:
                        Toast.makeText(mContext, "Third Clicked", Toast.LENGTH_SHORT).show();              //Drawer menu itemleri
                        return true;

                    case R.id.nav_settings:
                        Intent intent2=new Intent(HomeActivity.this,SettingsActivity.class);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_logout:
                        Paper.book().destroy();
                        Intent intent=new Intent(HomeActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();


                    default:
                        return true;

                }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    final  DatabaseReference favRef=FirebaseDatabase.getInstance().getReference().child("Favorite List");

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Product> options=new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(ProductsRef,Product.class)
                .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Product product) {



                        productViewHolder.txtproductName.setText(product.getName());
                        productViewHolder.txtproductDescription.setText(product.getDescription());
                        productViewHolder.txtproductPrice.setText(product.getPrice()+" $ ");
                        Picasso.get().load(product.getImage()).into(productViewHolder.imageView);





                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(HomeActivity.this,ProductDetailsActivity.class);
                                intent.putExtra("pid",product.getPid());
                                startActivity(intent);
                            }
                        });



                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.products_layout,parent,false);

                        ProductViewHolder holder=new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.home,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.search_icon){
            return true;
        }
        else if(id==R.id.notification_icon){

            return true;
        }
        return super.onOptionsItemSelected(item);



    }
}
