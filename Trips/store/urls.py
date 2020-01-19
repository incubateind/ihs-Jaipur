from django.contrib import admin
from django.urls import path
from . import views
urlpatterns = [
    path('', views.index, name="storeHome"),
    path('about/', views.about, name="storeHome"),
    path('blog/', views.blog, name="storeHome"),
    path('contact/', views.contact, name="storeHome"),
    path('dest/', views.dest, name="storeHome"),
    path('signin/', views.signin, name="storeHome"),
    path('trips/', views.trips, name="storeHome"),
    path('demo/', views.demo, name="storeHome"),
]
