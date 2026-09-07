package com.mrdiy.service;

import com.mrdiy.common.ApiException;
import com.mrdiy.domain.DeliveryAddress;
import com.mrdiy.domain.Favorite;
import com.mrdiy.domain.User;
import com.mrdiy.dto.SimpleDtos;
import com.mrdiy.repository.DeliveryAddressRepository;
import com.mrdiy.repository.FavoriteRepository;
import com.mrdiy.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {
    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;

    public CustomerService(FavoriteRepository favoriteRepository, ProductRepository productRepository,
                           DeliveryAddressRepository deliveryAddressRepository) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
        this.deliveryAddressRepository = deliveryAddressRepository;
    }

    @Transactional
    public Favorite addFavorite(User user, UUID productId) {
        if (favoriteRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Product already in favorite list");
        }
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(productRepository.findById(productId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found")));
        return favoriteRepository.save(favorite);
    }

    public List<Favorite> favorites(User user) {
        List<Favorite> favorites = favoriteRepository.findByUserId(user.getId());
        if (favorites.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No favorites found");
        }
        return favorites;
    }

    @Transactional
    public void removeFavorite(User user, UUID productId) {
        Favorite favorite = favoriteRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Favorite not found"));
        favoriteRepository.delete(favorite);
    }

    public DeliveryAddress createAddress(User user, SimpleDtos.DeliveryAddressRequest request) {
        DeliveryAddress address = new DeliveryAddress();
        address.setUser(user);
        address.setAddress(request.address);
        address.setCity(request.city);
        address.setState(request.state);
        return deliveryAddressRepository.save(address);
    }

    public List<DeliveryAddress> addresses(User user) {
        return deliveryAddressRepository.findByUserId(user.getId());
    }

    public DeliveryAddress updateAddress(User user, UUID id, SimpleDtos.DeliveryAddressRequest request) {
        DeliveryAddress address = deliveryAddressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Delivery address not found"));
        address.setAddress(request.address);
        address.setCity(request.city);
        address.setState(request.state);
        return deliveryAddressRepository.save(address);
    }

    public void deleteAddress(User user, UUID id) {
        DeliveryAddress address = deliveryAddressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Delivery address not found"));
        deliveryAddressRepository.delete(address);
    }
}
