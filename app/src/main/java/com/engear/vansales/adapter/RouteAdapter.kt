package com.engear.vansales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.interfaces.RouteOnClickListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.SupervisorRoute

class RouteAdapter(
    private var listRoutes: ArrayList<SupervisorRoute.Data>,
    private val onRouteOnClickListener: RouteOnClickListener,
    var menuSelection: Int,
    var db: DatabaseHandler?
) : RecyclerView.Adapter<RouteAdapter.UserViewHolder>() {
    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var routeName: TextView = view.findViewById(R.id.text_route)
        var ischeckview: ImageView = view.findViewById(R.id.ischeckview)
        var routeImageView = view.findViewById<ImageView>(R.id.image_route)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val userView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_route_layout, parent, false)
        return UserViewHolder(userView)
    }

    override fun getItemCount(): Int {
        return listRoutes.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val route = listRoutes[position]
        holder.routeName.text = route.route.routeCode + " - " + route.route.routeName
//        holder.routeImageView.setImageResource(route.getRouteImageId())
        if (menuSelection == 3) {
            var isPosted = db!!.getDailyFeildisPosted(route.routeId.toString())
            if (isPosted.equals("Y")) {
                holder.ischeckview.visibility = View.VISIBLE
            } else {
                holder.ischeckview.visibility = View.GONE
            }

        } else {
            holder.ischeckview.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            onRouteOnClickListener.onRouteClickListener(route)
        }

    }
}

