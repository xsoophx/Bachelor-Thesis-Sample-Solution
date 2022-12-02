package de.tu_chemnitz.restful.repositories

import de.tu_chemnitz.restful.data.PlaceEntity
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlaceRepository : CrudRepository<PlaceEntity, String> {

    @Query("{'partners': {'first': ?0}}")
    fun findAllByPartner(name: String): List<PlaceEntity>
}