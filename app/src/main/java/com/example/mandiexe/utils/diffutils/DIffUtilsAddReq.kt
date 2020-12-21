package com.example.mandiexe.utils.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.example.mandiexe.models.requirements.AddRequirementResponse


class DIffUtilsAddReq : DiffUtil.ItemCallback<AddRequirementResponse>() {

    //Separate Logic for the home memes. They will have same id
    //# We can do this, the user will not get same template (ID ) again
    override fun areItemsTheSame(
        oldItem: AddRequirementResponse,
        newItem: AddRequirementResponse
    ): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(
        oldItem: AddRequirementResponse,
        newItem: AddRequirementResponse
    ): Boolean {
        return oldItem == newItem
    }

}