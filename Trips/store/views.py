from django.shortcuts import render

# Create your views here.
def index(request):
    return render(request, 'store/index.html')

def blog(request):
    return render(request, 'store/blog.html')

def about(request):
    return render(request, 'store/about.html')

def contact(request):
    return render(request, 'store/contact.html')

def dest(request):
    return render(request, 'store/dest.html')

def signin(request):
    return render(request, 'store/signin.html')

def trips(request):
    return render(request, 'store/trips.html')

def demo(request):
    return render(request, 'store/demo.html')